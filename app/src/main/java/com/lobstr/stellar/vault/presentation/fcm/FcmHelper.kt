package com.lobstr.stellar.vault.presentation.fcm


import android.content.Context
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException.Action.DEFAULT
import com.lobstr.stellar.vault.domain.fcm.FcmInteractor
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

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

    fun checkFcmRegistration() {
        when (requestFcmTokenStatus()) {
            FcmRegStatus.TRYING_REGISTER_FCM -> createAndSendFcmToken()
        }
    }

    fun unregisterFcm() {
        fcmInteractor.getFcmToken()?.let {
            registerDevice(it, false)
        }
    }

    private fun requestFcmTokenStatus(): Int {
        // FCM registration (Check device for Play Services APK before, If check succeeds - proceed).
        return if (AppUtil.isGooglePlayServicesAvailable(context)) {
            val fcmToken = fcmInteractor.getFcmToken()
            if (fcmToken.isNullOrEmpty() || !fcmInteractor.isFcmRegistered()) {
                FcmRegStatus.TRYING_REGISTER_FCM
            } else {
                Log.i(LOG_TAG, "Device already registered with id - $fcmToken")
                FcmRegStatus.DEVICE_ALREADY_REGISTERED
            }
        } else {
            // If no - open app anyway.
            Log.e(LOG_TAG, "No valid Google Play Services APK found. ")
            FcmRegStatus.NO_VALID_GOOGLE_PLAY
        }
    }

    internal fun requestToRefreshFcmToken(newToken: String) {
        // Update token if needed (when device was registered).
        val fcmToken = fcmInteractor.getFcmToken()
        if (!fcmToken.isNullOrEmpty() && fcmToken != newToken) {
            fcmInteractor.setFcmRegistered(false)
            fcmInteractor.saveFcmToken(newToken)
            registerDevice(newToken)
        }
    }

    private fun createAndSendFcmToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val token = it.token
            fcmInteractor.saveFcmToken(token)
            Log.i(LOG_TAG, "Device registered: REG_ID = $token")
            registerDevice(token)
        }
    }

    private fun registerDevice(token: String, active: Boolean = true) {
        fcmInteractor.fcmDeviceRegistration(OS_TYPE, token, active)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                fcmInteractor.setFcmRegistered(active)
            }, {
                when (it) {
                    is UserNotAuthorizedException -> {
                        when (it.action) {
                            DEFAULT -> registerDevice(token)
                        }
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

    fun isUserAuthorized(): Boolean {
        return fcmInteractor.isUserAuthorized()
    }

    fun isNotificationsEnabled(): Boolean {
        return fcmInteractor.isNotificationsEnabled()
    }

    // Handle specific notifications

    fun signedNewAccount(jsonStr: String?): Account? {
        return fcmInteractor.signedNewAccount(jsonStr)
    }

    fun removedSigner(jsonStr: String?): Account? {
        return fcmInteractor.removedSigner(jsonStr)
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