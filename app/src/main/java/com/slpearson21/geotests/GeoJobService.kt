package com.slpearson21.geotests

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat

/**
 * Created by stephen.pearson on 3/28/18.
 *
 */
class GeoJobService : JobService() {

    override fun onStartJob(params: JobParameters): Boolean {
        val message = params.extras.getString(LOG_TEXT_EXTRA)
        sendNotification(this, message)

        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean = false

    private fun sendNotification(context: Context, content: String) {
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.status_bar_icon)
                .setContentTitle("Geofence Event")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(content)
                .setStyle(
                        NotificationCompat.BigTextStyle().bigText(content)
                )

        val newIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(context.applicationContext, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(resultPendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Geo notification channel",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        val LOG_TEXT_EXTRA = "log_text_extra"

        val NOTIFICATION_CHANNEL_ID = "geotests.geofence.notifications"
        val NOTIFICATION_ID = 888
    }
}