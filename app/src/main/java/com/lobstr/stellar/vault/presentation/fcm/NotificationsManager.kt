package com.lobstr.stellar.vault.presentation.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTION_DETAILS


class NotificationsManager(private val context: Context) {

    companion object {
        val NOTIFICATION_ID = 1
    }

    object ChanelId {
        val LV_MAIN = "LV_MAIN"
    }

    object ChanelName {
        val LV = "LV"
    }

    fun sendNotification(
        channelId: String, channelName: String,
        notificationTitle: String, notificationMessage: String?
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.action = java.lang.Long.toString(System.currentTimeMillis())
        val contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val mBuilder =
            NotificationCompat.Builder(context, createNotificationChannel(notificationManager, channelId, channelName))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLights(Color.BLUE, 500, 500)
                .setContentTitle(notificationTitle)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
                .setContentText(notificationMessage)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
        mBuilder.setContentIntent(contentIntent)

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
    }

    fun sendAddedNewTransactionNotification(
        channelId: String,
        channelName: String,
        notificationTitle: String,
        notificationMessage: String?,
        transactionItem: TransactionItem?,
        aClass: Class<*>
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val mBuilder = NotificationCompat.Builder(
            context,
            createNotificationChannel(notificationManager, channelId, channelName)
        )
            .setSmallIcon(R.mipmap.ic_launcher)
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
        getAddedNewTransactionPendingIntent(transactionItem, aClass)?.cancel()
        mBuilder.setContentIntent(getAddedNewTransactionPendingIntent(transactionItem, aClass))
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
    }

    // data - some data that we must pass to activity from fcm
    // and refresh this data when we will open activity
    private fun getAddedNewTransactionPendingIntent(
        transactionItem: TransactionItem?,
        targetClassName: Class<*>
    ): PendingIntent? {
        // Creates an explicit intent for an Activity in your app
        val notificationIntent = Intent(context, targetClassName)
        notificationIntent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, TRANSACTION_DETAILS)
        notificationIntent.putExtra(Constant.Extra.EXTRA_TRANSACTION_ITEM, transactionItem)
        notificationIntent.action = java.lang.Long.toString(System.currentTimeMillis())

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        val stackBuilder = TaskStackBuilder.create(context)

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(targetClassName)

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent)

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        channelId: String,
        channelName: String
    ): String {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        return channelId
    }

    fun clearNotifications(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancelAll()
    }
}