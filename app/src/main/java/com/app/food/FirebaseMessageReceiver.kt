package com.app.food

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.widget.RemoteViews
import android.content.Intent
import android.app.PendingIntent
import android.os.Build
import android.app.NotificationManager
import android.app.NotificationChannel
import androidx.core.app.NotificationCompat
import com.app.food.ui.MainActivity

class FirebaseMessageReceiver : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            showNotification(
                remoteMessage.notification!!.title,
                remoteMessage.notification!!.body
            )
        }
    }

    private fun getCustomDesign(
        title: String?,
        message: String?
    ): RemoteViews {
        val remoteViews = RemoteViews(
            applicationContext.packageName,
            R.layout.notification
        )
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(
            R.id.icon,
            R.mipmap.notif_icon
        )
        return remoteViews
    }

    private fun showNotification(
        title: String?,
        message: String?
    ) {
        val intent = Intent(this, MainActivity::class.java)
        val channelId = "notification_channel"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val builder = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
            .setSmallIcon(R.drawable.gcm_icon)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder.setContent(
            getCustomDesign(title, message)
        )
        val notificationManager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(
                channelId, "foodApp",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }
        notificationManager.notify(10, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}