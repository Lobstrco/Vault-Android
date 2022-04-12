package com.lobstr.stellar.vault.presentation.fcm

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException.Action.DEFAULT
import com.lobstr.stellar.vault.domain.fcm.FcmInteractor
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class FcmHelper(private val context: Context, private val fcmInteractor: FcmInteractor) {

    companion object {
        private val LOG_TAG = FcmHelper::class.simpleName
        private const val OS_TYPE = "android"
        private const val FCM_REGISTERED_ERROR = "error"
        private const val FCM_REGISTERED_SUCCESS = "success"
    }

    object FcmRegStatus {
        internal const val DEVICE_ALREADY_REGISTERED = 1
        internal const val NO_VALID_GOOGLE_PLAY = 2
        internal const val TRYING_REGISTER_FCM = 3
    }

    private var fsmDisposable: Disposable? = null

    fun checkFcmRegistration() {
        when (requestFcmTokenStatus()) {
            FcmRegStatus.TRYING_REGISTER_FCM -> createAndSendFcmToken()
        }
    }

    fun unregisterFcm() {
        fcmInteractor.getFcmToken()?.let {
            registerDevice(it, fcmInteractor.getAuthTokenList(true), false)
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

    internal fun requestToRefreshFcmToken(newFcmToken: String) {
        // Update token if needed (when device was registered).
        val fcmToken = fcmInteractor.getFcmToken()
        if (!fcmToken.isNullOrEmpty() && fcmToken != newFcmToken) {
            fcmInteractor.setFcmNotRegistered()
            fcmInteractor.saveFcmToken(newFcmToken)
            registerDevice(newFcmToken, fcmInteractor.getAuthTokenList())
        }
    }

    private fun createAndSendFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            val fcmToken = it
            if (fcmToken != fcmInteractor.getFcmToken()) {
                fcmInteractor.setFcmNotRegistered()
                fcmInteractor.saveFcmToken(fcmToken)
            }
            Log.i(LOG_TAG, "Device registered: REG_ID = $fcmToken")
            registerDevice(fcmToken, fcmInteractor.getAuthTokenList())
        }
    }

    private fun registerDevice(
        fcmToken: String,
        authTokenList: List<Pair<String, String>>,
        active: Boolean = true
    ) {
        fsmDisposable?.dispose()
        fsmDisposable = Observable.fromIterable(authTokenList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapSingle { authTokenInfo ->
                fcmInteractor.fcmDeviceRegistrationByToken(
                    OS_TYPE,
                    fcmToken,
                    active,
                    authTokenInfo.second,
                    authTokenInfo.first
                )
                    .map { Pair(authTokenInfo.first, FCM_REGISTERED_SUCCESS) }
                    .onErrorReturn {
                        when (it) {
                            is UserNotAuthorizedException -> {
                                when (it.action) {
                                    DEFAULT -> authTokenInfo
                                    else -> Pair(authTokenInfo.first, FCM_REGISTERED_ERROR)
                                }
                            }
                            is DefaultException -> {
                                Log.e(LOG_TAG, it.details)
                                Pair(authTokenInfo.first, FCM_REGISTERED_ERROR)
                            }
                            else -> {
                                Log.e(LOG_TAG, it.message ?: "")
                                Pair(authTokenInfo.first, FCM_REGISTERED_ERROR)
                            }
                        }
                    }
            }
            .toList()
            .subscribe(
                { resultList ->
                    resultList.filter { it.second == FCM_REGISTERED_SUCCESS }.forEach {
                        fcmInteractor.saveRegisteredFcmPublicKey(it.first, active)
                    }

                    resultList.filter { it.second.isEmpty() }.apply {
                        if (isNotEmpty()) {
                            registerDevice(fcmToken, fcmInteractor.getAuthTokenList(), active)
                        }
                    }
                }, {

                }
            )
    }

    fun isUserAuthorized(): Boolean = fcmInteractor.isUserAuthorized()

    fun isNotificationsEnabled(userAccount: String): Boolean = fcmInteractor.isNotificationsEnabled(userAccount)

    // Handle specific notifications

    fun userAccountReceived(jsonStr: String?): String? = fcmInteractor.userAccountReceived(jsonStr)

    fun signedNewAccount(jsonStr: String?): Account? = fcmInteractor.signedNewAccount(jsonStr)

    fun removedSigner(jsonStr: String?): Account? = fcmInteractor.removedSigner(jsonStr)

    fun getCurrentPublicKey(): String? = fcmInteractor.getCurrentPublicKey()

    fun addedNewTransaction(jsonStr: String?): TransactionItem? =
        fcmInteractor.transformTransactionResponse(jsonStr)

    fun addedNewSignature(jsonStr: String?): TransactionItem? =
        fcmInteractor.transformTransactionResponse(jsonStr)

    fun transactionSubmitted(jsonStr: String?): TransactionItem? =
        fcmInteractor.transformTransactionResponse(jsonStr)
}