package com.lobstr.stellar.vault.presentation.fcm

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dagger.module.fcm.FcmServiceModule
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.ACCOUNT
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.EVENT_TYPE
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.MESSAGE_BODY
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.MESSAGE_TITLE
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.TRANSACTION
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChanelId.LV_MAIN
import com.lobstr.stellar.vault.presentation.splash.SplashActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import javax.inject.Inject


class LVFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var mFcmHelper: FcmHelper

    @Inject
    lateinit var mEventProviderModule: EventProviderModule

    companion object {
        val LOG_TAG = LVFirebaseMessagingService::class.simpleName
    }

    object Field {
        const val MESSAGE_BODY = "message_body"
        const val MESSAGE_TITLE = "message_title"
        const val EVENT_TYPE = "event_type"
        const val ACCOUNT = "account"
        const val TRANSACTION = "transaction"
    }

    object Type {
        const val SIGNED_NEW_ACCOUNT = "signed_new_account"
        const val ADDED_NEW_TRANSACTION = "added_new_transaction"
    }

    init {
        LVApplication.sAppComponent.plusFcmServiceComponent(FcmServiceModule()).inject(this)
    }

    override fun onNewToken(newToken: String?) {
        super.onNewToken(newToken)
        Log.d(LOG_TAG, newToken)
        mFcmHelper.requestToRefreshFcmToken()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        if (!mFcmHelper.isUserAuthorized()) {
            return
        }

        val data = remoteMessage!!.data
        if (data.isNotEmpty()) {
            parseData(data)
        }
    }

    private fun parseData(data: Map<*, *>?) {
        if (data == null || data.isEmpty()) {
            return
        }

        try {

            val messageTitle: String? = data[MESSAGE_TITLE] as? String
            val eventType: String? = data[EVENT_TYPE] as? String
            val messageBody: String? = data[MESSAGE_BODY] as? String

            val notificationsManager = NotificationsManager(this)

            when (eventType) {

                Type.SIGNED_NEW_ACCOUNT -> wrapSignedNewAccountMessage(
                    data[ACCOUNT] as? String, messageTitle, messageBody, notificationsManager
                )

                Type.ADDED_NEW_TRANSACTION -> wrapAddedNewTransactionMessage(
                    data[TRANSACTION] as? String, messageTitle, messageBody, notificationsManager
                )

                else -> sendDefaultMessage(messageTitle, messageBody, notificationsManager)
            }

        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    private fun wrapSignedNewAccountMessage(
        jsonStr: String?,
        messageTitle: String?,
        messageBody: String?,
        notificationsManager: NotificationsManager
    ) {
        val account = mFcmHelper.signedNewAccount(jsonStr)
        mEventProviderModule.notificationEventSubject.onNext(
            Notification(Notification.Type.SIGNED_NEW_ACCOUNT, account)
        )

        // TODO show by default Splash screen, otherwise - HomeActivity . Fix caused by security reasons (click notification on Pin Screen)
        sendDefaultMessage(
            messageTitle,
            messageBody,
            notificationsManager/*,
            HomeActivity::class.java*/
        )
    }

    private fun wrapAddedNewTransactionMessage(
        jsonStr: String?,
        messageTitle: String?,
        messageBody: String?,
        notificationsManager: NotificationsManager
    ) {
        val transaction = mFcmHelper.addedNewTransaction(jsonStr)
        mEventProviderModule.notificationEventSubject.onNext(
            Notification(Notification.Type.ADDED_NEW_TRANSACTION, transaction)
        )

        if (!mFcmHelper.isNotificationsEnabled()) {
            return
        }

        // show transaction details screen after click on notification
        val intent = Intent(this, ContainerActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.TRANSACTION_DETAILS)
        intent.putExtra(Constant.Extra.EXTRA_TRANSACTION_ITEM, transaction)

        // TODO show by default Splash screen, otherwise - send intent. Fix caused by security reasons (click notification on Pin Screen)
        notificationsManager.sendNotification(
            LV_MAIN,
            getString(R.string.app_name),
            messageTitle ?: getString(R.string.app_name),
            messageBody,
            /*intent*/SplashActivity::class.java
        )
    }

    private fun sendDefaultMessage(
        messageTitle: String?,
        messageBody: String?,
        notificationsManager: NotificationsManager,
        targetClass: Class<*> = SplashActivity::class.java
    ) {
        if (!mFcmHelper.isNotificationsEnabled()) {
            return
        }

        if (TextUtils.isEmpty(messageBody)) {
            return
        }

        notificationsManager.sendNotification(
            LV_MAIN,
            getString(R.string.app_name),
            messageTitle ?: getString(R.string.app_name),
            messageBody,
            targetClass
        )
    }
}