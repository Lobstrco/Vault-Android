package com.lobstr.stellar.vault.presentation.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.AUTHORIZED_TRANSACTIONS
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.INCOMING_TRANSACTIONS
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.LV_MAIN
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.NEW_SIGNATURES
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.OTHER
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.SIGNER_STATUS


class NotificationsManager(private val context: Context) {

    companion object {
        fun clearNotifications(context: Context) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancelAll()
        }
    }

    object ChannelId {
        const val LV_MAIN = "LV_MAIN"
        const val SIGNER_STATUS = "Signer status"
        const val INCOMING_TRANSACTIONS = "Incoming transactions"
        const val NEW_SIGNATURES = "New signatures"
        const val AUTHORIZED_TRANSACTIONS = "Authorized transactions"
        const val OTHER = "Other"
    }

    // must be unique (not equal GroupId)
    object NotificationId {
        const val LV_MAIN = 0
        const val SIGNERS_COUNT_CHANGED = 1
    }

    // must be unique (not equal NotificationId)
    object GroupId {
        const val LV_MAIN = 2
        const val SIGNER_STATUS = 3
        const val TRANSACTION_HISTORY = 4
        const val NEW_SIGNATURES = 5
        const val AUTHORIZED_TRANSACTIONS = 6
        const val OTHER = 7
    }

    object GroupName {
        const val LV_MAIN = "LOBSTR Vault Main"
        const val SIGNER_STATUS = "Signer Status"
        const val TRANSACTION_HISTORY = "Transaction History"
        const val NEW_SIGNATURES = "New Signatures"
        const val AUTHORIZED_TRANSACTIONS = "Authorized Transactions"
        const val OTHER = "Other"
    }

    /**
     * Show only specific activity
     */
    fun sendNotification(
        channelId: String,
        notificationId: Int,
        notificationTitle: String,
        notificationMessage: String?,
        groupId: Int,
        groupName: String,
        targetClass: Class<*>
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

        val intent = Intent(context, targetClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.action = System.currentTimeMillis().toString()
        val contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder =
            NotificationCompat.Builder(context, createNotificationChannel(channelId))
                .setSmallIcon(R.drawable.ic_stat_notif)
                .setLights(Color.BLUE, 500, 500)
                .setContentTitle(notificationTitle)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
                .setContentText(notificationMessage)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
        notificationBuilder.setContentIntent(contentIntent)

        // notifications group with NotificationCompat.InboxStyle() starting Android N
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder.setGroup(groupName)

            val groupBuilder =
                NotificationCompat.Builder(
                    context,
                    createNotificationChannel(channelId)
                )
                    .setSmallIcon(R.drawable.ic_stat_notif)
                    .setLights(Color.BLUE, 500, 500)
                    .setContentTitle(notificationTitle)
                    .setStyle(NotificationCompat.InboxStyle())
                    .setContentText(notificationMessage)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroupSummary(true)
                    .setGroup(groupName)
                    .setAutoCancel(true)

            notificationManager.notify(groupId, groupBuilder.build())
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    /**
     * Show specific activity with extra
     */
    fun sendNotification(
        channelId: String,
        notificationId: Int,
        notificationTitle: String,
        notificationMessage: String?,
        groupId: Int,
        groupName: String,
        intent: Intent
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

        val notificationBuilder = NotificationCompat.Builder(
            context,
            createNotificationChannel(channelId)
        )
            .setSmallIcon(R.drawable.ic_stat_notif)
            .setLights(Color.BLUE, 500, 500)
            .setContentTitle(notificationTitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
            .setContentText(notificationMessage)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)

        // data (image url or something else) - some data that we must pass to activity from fcm
        // and refresh this data when we will open activity
        // in KitKat and Lollipop PendingIntent.FLAG_UPDATE_CURRENT not working correct
        // (after first time) and this trick helps to solve the problem
        getPendingIntent(intent)?.cancel()
        notificationBuilder.setContentIntent(getPendingIntent(intent))

        // notifications group with NotificationCompat.InboxStyle() starting Android N
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder.setGroup(groupName)

            val groupBuilder =
                NotificationCompat.Builder(
                    context,
                    createNotificationChannel(channelId)
                )
                    .setSmallIcon(R.drawable.ic_stat_notif)
                    .setLights(Color.BLUE, 500, 500)
                    .setContentTitle(notificationTitle)
                    .setStyle(NotificationCompat.InboxStyle())
                    .setContentText(notificationMessage)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroupSummary(true)
                    .setGroup(groupName)
                    .setAutoCancel(true)

            notificationManager.notify(groupId, groupBuilder.build())
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    // notificationIntent - some data that we must pass from fcm
    // and refresh this data when we will open activity
    private fun getPendingIntent(
        notificationIntent: Intent
    ): PendingIntent? {
        // Creates an explicit intent for an Activity in your app
        notificationIntent.action = System.currentTimeMillis().toString()

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        val stackBuilder = TaskStackBuilder.create(context)

        // Adds the back stack for the Intent (but not the Intent itself)
        // NOTE: don't worked for 'qa' product flavor
        stackBuilder.addParentStack(notificationIntent.component)

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent)

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createNotificationChannel(
        channelId: String,
        notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    ): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, createChannelName(channelId), importance)
            notificationChannel.description = createChannelDescription(channelId)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        return channelId
    }

    private fun createChannelName(chanelId: String): String {
        return when (chanelId) {
            LV_MAIN -> context.getString(R.string.app_name)
            SIGNER_STATUS -> context.getString(R.string.channel_signer_status)
            INCOMING_TRANSACTIONS -> context.getString(R.string.channel_incoming_transactions)
            NEW_SIGNATURES -> context.getString(R.string.channel_new_signatures)
            AUTHORIZED_TRANSACTIONS -> context.getString(R.string.channel_authorized_transactions)
            OTHER -> context.getString(R.string.channel_other)
            else -> context.getString(R.string.app_name)
        }
    }

    private fun createChannelDescription(chanelId: String): String? {
        // add channel descriptions
        return when (chanelId) {
            else -> null
        }
    }
}