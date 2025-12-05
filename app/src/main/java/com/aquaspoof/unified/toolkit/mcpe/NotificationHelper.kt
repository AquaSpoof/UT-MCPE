package com.aquaspoof.unified.toolkit.mcpe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "download_channel"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.download_notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildProgressNotification(filename: String, progress: Int): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.download_notification_title))
            .setContentText(filename)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setProgress(100, progress, false)
            .setOnlyAlertOnce(true)
    }

    fun showCompleteNotification(notificationId: Int, filename: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.download_complete))
            .setContentText(filename)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
        notificationManager.notify(notificationId, builder.build())
    }

    fun showFailedNotification(notificationId: Int, filename: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.download_failed))
            .setContentText(filename)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
        notificationManager.notify(notificationId, builder.build())
    }

    fun getManager() = notificationManager
}