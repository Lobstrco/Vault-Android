package com.lobstr.stellar.vault.presentation.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.AUTHORIZED_TRANSACTIONS
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.INCOMING_TRANSACTIONS
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.LV_MAIN
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.NEW_SIGNATURES
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.OTHER
import com.lobstr.stellar.vault.presentation.fcm.NotificationsManager.ChannelId.SIGNER_STATUS
import com.lobstr.stellar.vault.presentation.util.Constant

class NotificationsManager(private val context: Context) {

    companion object {
        fun clearNotifications(context: Context) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancelAll()
        }
    }

    // NOTE Don't forget to apply Channel name for the created channel [see createChannelName()].
    object ChannelId {
        const val LV_MAIN = "LV_MAIN"
        const val SIGNER_STATUS = "Signer status"
        const val INCOMING_TRANSACTIONS = "Incoming transactions"
        const val NEW_SIGNATURES = "New signatures"
        const val AUTHORIZED_TRANSACTIONS = "Authorized transactions"
        const val OTHER = "Other"
    }

    // Must be unique (not equal GroupId).
    object NotificationId {
        const val LV_MAIN = 0
        const val SIGNERS_COUNT_CHANGED = 1
    }

    // Must be unique (not equal NotificationId).
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
     * Show only specific activity.
     * @param importance Importance level for the notification channel (Android 8.0 and higher). Use [NotificationManagerCompat] constants.
     * @param priority Notification priority (Android 7.1 and lower).
     */
    fun sendNotification(
        channelId: String,
        notificationId: Int,
        notificationTitle: String,
        notificationMessage: String?,
        groupId: Int,
        groupName: String,
        notificationIntentClass: Class<*>,
        groupIntentClass: Class<*>,
        importance: Int = NotificationManagerCompat.IMPORTANCE_HIGH,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

        val notificationBuilder =
            NotificationCompat.Builder(
                context,
                createNotificationChannel(channelId, notificationManager, importance)
            )
                .setSmallIcon(R.drawable.ic_stat_notif)
                .setColor(
                    ContextCompat.getColor(
                        context,
                        if (BuildConfig.FLAVOR.equals(Constant.Flavor.VAULT)) R.color.color_primary else R.color.color_757575
                    )
                )
                .setLights(
                    ContextCompat.getColor(
                        context,
                        if (BuildConfig.FLAVOR.equals(Constant.Flavor.VAULT)) R.color.color_primary else R.color.color_757575
                    ), 500, 500
                )
                .setContentTitle(notificationTitle)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
                .setContentText(notificationMessage)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setPriority(priority)
        notificationBuilder.setContentIntent(getPendingIntent(notificationIntentClass))

        // Notifications group with NotificationCompat.InboxStyle() starting Android N.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder.setGroup(groupName)

            val groupBuilder =
                NotificationCompat.Builder(
                    context,
                    createNotificationChannel(channelId, notificationManager, importance)
                )
                    .setSmallIcon(R.drawable.ic_stat_notif)
                    .setColor(
                        ContextCompat.getColor(
                            context,
                            if (BuildConfig.FLAVOR.equals(Constant.Flavor.VAULT)) R.color.color_primary else R.color.color_757575
                        )
                    )
                    .setLights(
                        ContextCompat.getColor(
                            context,
                            if (BuildConfig.FLAVOR.equals(Constant.Flavor.VAULT)) R.color.color_primary else R.color.color_757575
                        ), 500, 500
                    )
                    .setContentTitle(notificationTitle)
                    .setStyle(NotificationCompat.InboxStyle())
                    .setContentText(notificationMessage)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroupSummary(true)
                    .setGroup(groupName)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setAutoCancel(true)
                    .setPriority(priority)
                    .setContentIntent(getPendingIntent(groupIntentClass))

            notificationManager.notify(groupId, groupBuilder.build())
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    /**
     * Show specific activity with extra.
     * @param importance Importance level for the notification channel (Android 8.0 and higher). Use [NotificationManagerCompat] constants.
     * @param priority Notification priority (Android 7.1 and lower).
     */
    fun sendNotification(
        channelId: String,
        notificationId: Int,
        notificationTitle: String,
        notificationMessage: String?,
        groupId: Int,
        groupName: String,
        intent: Intent,
        groupIntent: Intent,
        importance: Int = NotificationManagerCompat.IMPORTANCE_HIGH,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

        val notificationBuilder = NotificationCompat.Builder(
            context,
            createNotificationChannel(channelId, notificationManager, importance)
        )
            .setSmallIcon(R.drawable.ic_stat_notif)
            .setColor(
                ContextCompat.getColor(
                    context,
                    if (BuildConfig.FLAVOR.equals(Constant.Flavor.VAULT)) R.color.color_primary else R.color.color_757575
                )
            )
            .setLights(
                ContextCompat.getColor(
                    context,
                    if (BuildConfig.FLAVOR.equals(Constant.Flavor.VAULT)) R.color.color_primary else R.color.color_757575
                ), 500, 500
            )
            .setContentTitle(notificationTitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
            .setContentText(notificationMessage)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .setPriority(priority)

        // Data (image url or something else) - some data that we must pass to activity from fcm
        // and refresh this data when we will open activity.
        // In KitKat and Lollipop PendingIntent.FLAG_UPDATE_CURRENT not working correct
        // (after first time) and this trick helps to solve the problem.
        getPendingIntentWithStack(intent)?.cancel()
        notificationBuilder.setContentIntent(getPendingIntentWithStack(intent))

        // Notifications group with NotificationCompat.InboxStyle() starting Android N.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder.setGroup(groupName)

            val groupBuilder =
                NotificationCompat.Builder(
                    context,
                    createNotificationChannel(channelId, notificationManager, importance)
                )
                    .setSmallIcon(R.drawable.ic_stat_notif)
                    .setColor(
                        ContextCompat.getColor(
                            context,
                            if (BuildConfig.FLAVOR.equals(Constant.Flavor.VAULT)) R.color.color_primary else R.color.color_757575
                        )
                    )
                    .setLights(
                        ContextCompat.getColor(
                            context,
                            if (BuildConfig.FLAVOR.equals(Constant.Flavor.VAULT)) R.color.color_primary else R.color.color_757575
                        ), 500, 500
                    )
                    .setContentTitle(notificationTitle)
                    .setStyle(NotificationCompat.InboxStyle())
                    .setContentText(notificationMessage)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroupSummary(true)
                    .setGroup(groupName)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setAutoCancel(true)
                    .setPriority(priority)
                    .setContentIntent(getPendingIntent(groupIntent))

            notificationManager.notify(groupId, groupBuilder.build())
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun getPendingIntent(intent: Intent): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            intent.apply { action = System.currentTimeMillis().toString() },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun getPendingIntent(targetClassName: Class<*>): PendingIntent {
        return PendingIntent.getActivity(context, 0, Intent(context, targetClassName).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            action = System.currentTimeMillis().toString()
        }, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    // NotificationIntent - some data that we must pass from fcm
    // and refresh this data when we will open activity.
    private fun getPendingIntentWithStack(
        intent: Intent
    ): PendingIntent? {
        // Creates an explicit intent for an Activity in your app.
        intent.action = System.currentTimeMillis().toString()

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        val stackBuilder = TaskStackBuilder.create(context)

        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(intent.component)

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(intent)

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * @param importance Importance level for the notification channel (Android 8.0 and higher). Use [NotificationManagerCompat] constants.
     */
    private fun createNotificationChannel(
        channelId: String,
        notificationManager: NotificationManagerCompat,
        importance: Int = NotificationManagerCompat.IMPORTANCE_HIGH
    ): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                createChannelName(channelId),
                when (importance) {
                    NotificationManagerCompat.IMPORTANCE_NONE -> NotificationManager.IMPORTANCE_NONE
                    NotificationManagerCompat.IMPORTANCE_MIN -> NotificationManager.IMPORTANCE_MIN
                    NotificationManagerCompat.IMPORTANCE_LOW -> NotificationManager.IMPORTANCE_LOW
                    NotificationManagerCompat.IMPORTANCE_DEFAULT -> NotificationManager.IMPORTANCE_DEFAULT
                    NotificationManagerCompat.IMPORTANCE_HIGH -> NotificationManager.IMPORTANCE_HIGH
                    NotificationManagerCompat.IMPORTANCE_MAX -> NotificationManager.IMPORTANCE_MAX
                    else -> NotificationManager.IMPORTANCE_UNSPECIFIED
                }
            )
            notificationChannel.description = createChannelDescription(channelId)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
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
        // Add channel descriptions.
        return when (chanelId) {
            else -> null
        }
    }
}