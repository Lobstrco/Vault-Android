package com.lobstr.stellar.vault.presentation.fcm

import android.content.Intent
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.ACCOUNT
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.EVENT_TYPE
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.MESSAGE_BODY
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.MESSAGE_TITLE
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.TRANSACTION
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
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
        const val REMOVED_SIGNER = "removed_signer"
        const val ADDED_NEW_TRANSACTION = "added_new_transaction"
        const val ADDED_NEW_SIGNATURE = "added_new_signature"
        const val TRANSACTION_SUBMITTED = "transaction_submitted"
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        mFcmHelper.requestToRefreshFcmToken(newToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (!mFcmHelper.isUserAuthorized()) {
            return
        }

        parseData(remoteMessage.data)
    }

    private fun parseData(data: Map<String, String>?) {
        if (data.isNullOrEmpty()) {
            return
        }

        try {
            val messageTitle: String? = data[MESSAGE_TITLE]
            val eventType: String? = data[EVENT_TYPE]
            val messageBody: String? = data[MESSAGE_BODY]

            val notificationsManager = NotificationsManager(this)

            when (eventType) {

                Type.SIGNED_NEW_ACCOUNT -> wrapSignedNewAccountMessage(
                    data[ACCOUNT], messageTitle, messageBody, notificationsManager
                )

                Type.REMOVED_SIGNER -> wrapRemovedSignerMessage(
                    data[ACCOUNT], messageTitle, messageBody, notificationsManager
                )

                Type.ADDED_NEW_TRANSACTION -> wrapAddedNewTransactionMessage(
                    data[TRANSACTION], messageTitle, messageBody, notificationsManager
                )

                Type.ADDED_NEW_SIGNATURE -> wrapAddedNewSignatureMessage(
                    data[TRANSACTION], messageTitle, messageBody, notificationsManager
                )

                Type.TRANSACTION_SUBMITTED -> wrapTransactionSubmittedMessage(
                    data[TRANSACTION], messageTitle, messageBody, notificationsManager
                )

                else -> sendDefaultMessage(
                    Random.nextInt(),
                    NotificationsManager.ChannelId.OTHER,
                    messageTitle,
                    messageBody,
                    NotificationsManager.GroupId.OTHER,
                    NotificationsManager.GroupName.OTHER,
                    notificationsManager
                )
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

        sendDefaultMessage(
            NotificationsManager.NotificationId.SIGNERS_COUNT_CHANGED,
            NotificationsManager.ChannelId.SIGNER_STATUS,
            messageTitle,
            messageBody,
            NotificationsManager.GroupId.SIGNER_STATUS,
            NotificationsManager.GroupName.SIGNER_STATUS,
            notificationsManager
        )
    }

    private fun wrapRemovedSignerMessage(
        jsonStr: String?,
        messageTitle: String?,
        messageBody: String?,
        notificationsManager: NotificationsManager
    ) {
        val account = mFcmHelper.removedSigner(jsonStr)
        mEventProviderModule.notificationEventSubject.onNext(
            Notification(Notification.Type.REMOVED_SIGNER, account)
        )

        sendDefaultMessage(
            NotificationsManager.NotificationId.SIGNERS_COUNT_CHANGED,
            NotificationsManager.ChannelId.SIGNER_STATUS,
            messageTitle,
            messageBody,
            NotificationsManager.GroupId.SIGNER_STATUS,
            NotificationsManager.GroupName.SIGNER_STATUS,
            notificationsManager
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

        notificationsManager.sendNotification(
            NotificationsManager.ChannelId.INCOMING_TRANSACTIONS,
            NotificationsManager.NotificationId.LV_MAIN,
            messageTitle ?: getString(R.string.app_name),
            messageBody,
            NotificationsManager.GroupId.LV_MAIN,
            NotificationsManager.GroupName.LV_MAIN,
            // Show transaction details screen after click on notification.
            Intent(this, ContainerActivity::class.java).apply {
                putExtra(
                    Constant.Extra.EXTRA_NAVIGATION_FR,
                    Constant.Navigation.TRANSACTION_DETAILS
                )
                putExtra(Constant.Extra.EXTRA_TRANSACTION_ITEM, transaction)
            },
            Intent(this, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            },
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    private fun wrapAddedNewSignatureMessage(
        jsonStr: String?,
        messageTitle: String?,
        messageBody: String?,
        notificationsManager: NotificationsManager
    ) {
        val transaction = mFcmHelper.addedNewSignature(jsonStr)
        mEventProviderModule.notificationEventSubject.onNext(
            Notification(Notification.Type.ADDED_NEW_SIGNATURE, transaction)
        )

        sendDefaultMessage(
            NotificationsManager.NotificationId.LV_MAIN,
            NotificationsManager.ChannelId.NEW_SIGNATURES,
            messageTitle,
            messageBody,
            NotificationsManager.GroupId.LV_MAIN,
            NotificationsManager.GroupName.LV_MAIN,
            notificationsManager
        )
    }

    private fun wrapTransactionSubmittedMessage(
        jsonStr: String?,
        messageTitle: String?,
        messageBody: String?,
        notificationsManager: NotificationsManager
    ) {
        val transaction = mFcmHelper.transactionSubmitted(jsonStr)
        mEventProviderModule.notificationEventSubject.onNext(
            Notification(Notification.Type.TRANSACTION_SUBMITTED, transaction)
        )

        sendDefaultMessage(
            NotificationsManager.NotificationId.LV_MAIN,
            NotificationsManager.ChannelId.AUTHORIZED_TRANSACTIONS,
            messageTitle,
            messageBody,
            NotificationsManager.GroupId.LV_MAIN,
            NotificationsManager.GroupName.LV_MAIN,
            notificationsManager
        )
    }

    /**
     * @param importance Importance level for the notification channel (Android 8.0 and higher). Use [NotificationManagerCompat] constants.
     * @param priority Notification priority (Android 7.1 and lower).
     */
    private fun sendDefaultMessage(
        notificationId: Int,
        channelId: String,
        messageTitle: String?,
        messageBody: String?,
        groupId: Int,
        groupName: String,
        notificationsManager: NotificationsManager,
        notificationIntentClass: Class<*> = HomeActivity::class.java,
        groupIntentClass: Class<*> = HomeActivity::class.java,
        importance: Int = NotificationManagerCompat.IMPORTANCE_HIGH,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {
        if (!mFcmHelper.isNotificationsEnabled()) {
            return
        }

        if (TextUtils.isEmpty(messageBody)) {
            return
        }

        notificationsManager.sendNotification(
            channelId,
            notificationId,
            messageTitle ?: getString(R.string.app_name),
            messageBody,
            groupId,
            groupName,
            notificationIntentClass,
            groupIntentClass,
            importance,
            priority
        )
    }
}