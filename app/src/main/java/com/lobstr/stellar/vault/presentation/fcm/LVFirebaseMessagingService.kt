package com.lobstr.stellar.vault.presentation.fcm

import android.text.TextUtils
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dager.module.fcm.FcmServiceModule
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.DATA
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.MESSAGE
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService.Field.TYPE
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChanelId.LV_MAIN
import javax.inject.Inject


class LVFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var mFcmHelper: FcmHelper

    companion object {
        val LOG_TAG = LVFirebaseMessagingService::class.simpleName
    }

    object Field {
        const val DATA = "data"
        const val TYPE = "Type"
        const val MESSAGE = "Message"
    }

    object Type {
        const val SOME_TYPE = "SOME_TYPE"
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
            val rootObject = JsonParser().parse(data[DATA] as String)
                .asJsonObject
            val message = rootObject.asJsonObject.get(MESSAGE)
            val type = rootObject.get(TYPE)
            val messageToSend = message?.asString

            val notificationsManager = NotificationsManager(this)

            when (type.asString) {

                Type.SOME_TYPE -> wrapSomeTypeMessage(rootObject, messageToSend, notificationsManager)

                else -> wrapDefaultMessage(messageToSend, notificationsManager)
            }

        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    private fun wrapSomeTypeMessage(
        rootObject: JsonObject,
        messageToSend: String?,
        notificationsManager: NotificationsManager
    ) {
        // TODO add same logic
    }

    private fun wrapDefaultMessage(
        messageToSend: String?,
        notificationsManager: NotificationsManager
    ) {
        if (TextUtils.isEmpty(messageToSend)) {
            return
        }

        notificationsManager.sendNotification(
            LV_MAIN,
            getString(R.string.app_name),
            getString(R.string.app_name),
            messageToSend!!
        )
    }
}