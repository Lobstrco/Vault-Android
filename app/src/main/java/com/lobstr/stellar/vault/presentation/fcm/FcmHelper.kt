package com.lobstr.stellar.vault.presentation.fcm


import android.content.Context
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.iid.FirebaseInstanceId
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.fcm.FcmInteractor
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class FcmHelper(private val context: Context, private val fcmInteractor: FcmInteractor) {

    companion object {
        val LOG_TAG = FcmHelper::class.simpleName
        const val OS_TYPE = "android"

    }

    object FcmRegStatus {
        internal const val DEVICE_ALREADY_REGISTERED = 1
        internal const val NO_VALID_GOOGLE_PLAY = 2
        internal const val TRYING_REGISTER_FCM = 3
    }

    fun checkIfFcmRegisteredSuccessfully() {
        if (!fcmInteractor.isFcmRegistered()) {
            val registrationStatus = requestFcmToken()
            when (registrationStatus) {
                FcmRegStatus.TRYING_REGISTER_FCM, FcmHelper.FcmRegStatus.DEVICE_ALREADY_REGISTERED -> {
                    val fcmDeviceId = getSavedFcmToken()
                    if (!fcmDeviceId.isEmpty()) {
                        sendFcmToken(fcmDeviceId)
                    }
                }
            }
        }
    }

    private fun requestFcmToken(): Int {
        // FCM registration (Check device for Play Services APK before, If check succeeds - proceed)
        if (checkIsGooglePlayServicesAvailable()) {
            val fcmDeviceId = getSavedFcmToken()
            if (fcmDeviceId.isEmpty()) {
                createFcmToken()
                return FcmRegStatus.TRYING_REGISTER_FCM
            } else {
                // if device already registered on FCM  - send its id on server
                // it can happen e.g. when two or more users use one device
                Log.i(LOG_TAG, "Device already registered with id - $fcmDeviceId")
                return FcmRegStatus.DEVICE_ALREADY_REGISTERED
            }
        } else {
            // if no - open app anyway
            Log.e(LOG_TAG, "No valid Google Play Services APK found. ")
            return FcmRegStatus.NO_VALID_GOOGLE_PLAY
        }
    }

    internal fun requestToRefreshFcmToken() {
        if (checkIsGooglePlayServicesAvailable()) {
            createFcmToken()
            val fcmToken = getSavedFcmToken()
            if (!fcmToken.isEmpty() /*&& !TextUtils.isEmpty(PrefsUtils.getUserToken())*/) {
                sendFcmToken(fcmToken)
            }
        }
    }

    private fun createFcmToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val token = it.token
            saveFcmTokenAndAppVersion(token)
            Log.i(LOG_TAG, "Device registered: REG_ID = $token")
        }
    }

    private fun sendFcmToken(token: String?) {
        if (token != null && !token.isEmpty()) {
            fcmInteractor.fcmDeviceRegistration(OS_TYPE, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    fcmInteractor.setFcmRegistered(true)
                }, {
                    when (it) {
                        is UserNotAuthorizedException -> {
                            sendFcmToken(token)
                        }
                        is DefaultException -> {
                            Log.e(LOG_TAG, it.details)
                        }
                        else -> {
                            Log.e(LOG_TAG, it.message ?: "")
                        }
                    }
                })
        }
    }

    private fun saveFcmTokenAndAppVersion(token: String) {
        val appVersion = AppUtil.getAppVersionCode(context)
        fcmInteractor.saveFcmToken(token)
        fcmInteractor.saveAppVersion(appVersion)
    }

    private fun getSavedFcmToken(): String {
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new app version.
        val appVersion = fcmInteractor.getAppVersion()
        val currentVersion = AppUtil.getAppVersionCode(context)
        if (appVersion != currentVersion) {
            Log.i(LOG_TAG, "App version changed. ")
            return ""
        }

        val fcmToken = fcmInteractor.getFcmToken()
        if (fcmToken != null && !fcmToken.isEmpty()) {
            Log.i(LOG_TAG, "Previous registration was not found, register device on FCM")
            return fcmToken
        }

        return ""
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private fun checkIsGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Log.e(LOG_TAG, "No valid Google Play Services APK found. ")
            } else {
                Log.e(LOG_TAG, "This device is not supported. ")
            }

            return false
        }

        return true
    }

    fun isUserAuthorized(): Boolean {
        return fcmInteractor.isUserAuthorized()
    }

    fun isNotificationsEnabled(): Boolean {
        return fcmInteractor.isNotificationsEnabled()
    }

    // Handle specific notifications

    fun signedNewAccount(jsonStr: String?): Account? {
        return fcmInteractor.confirmIsUserSignerForLobstr(jsonStr)
    }

    fun addedNewTransaction(jsonStr: String?): TransactionItem? {
        return fcmInteractor.transformTransactionResponse(jsonStr)
    }

    fun addedNewSignature(jsonStr: String?): TransactionItem? {
        return fcmInteractor.transformTransactionResponse(jsonStr)
    }

    fun transactionSubmitted(jsonStr: String?): TransactionItem? {
        return fcmInteractor.transformTransactionResponse(jsonStr)
    }
}