package com.duwna.alarmable.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.*
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.duwna.alarmable.R
import com.duwna.alarmable.data.database.alarm.Alarm
import com.duwna.alarmable.ui.InfoActivity
import com.duwna.alarmable.ui.TaskActivity
import com.duwna.alarmable.utils.uriOrNull

class AlarmService : Service() {

    private lateinit var player: MediaPlayer

    override fun onStartCommand(serviceIntent: Intent, flags: Int, startId: Int): Int {

        val alarm = serviceIntent.getParcelableExtra<Alarm>(Alarm.KEY)

        showNotification(alarm?.id ?: startId, serviceIntent, alarm?.hasTask ?: false)
        playSound(alarm?.melodyUri.uriOrNull())

        return START_STICKY
    }

    private fun showNotification(startId: Int, serviceIntent: Intent, hasTask: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(this)
        val notification = createNotification(this, serviceIntent, hasTask)
        startForeground(startId, notification)
    }

    private fun playSound(melodyUri: Uri?) {
        player = if (melodyUri != null) MediaPlayer.create(this, melodyUri)
        else MediaPlayer.create(this, R.raw.alarm)

        player.apply {
            isLooping = true
            setVolume(100f, 100f)
            start()
        }

    }

    override fun onDestroy() {
        player.stop()
        player.release()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(
        context: Context,
        serviceIntent: Intent,
        hasTask: Boolean
    ): Notification {

        val contentIntent = Intent(
            this,
            if (hasTask) TaskActivity::class.java else InfoActivity::class.java
        ).apply { putExtras(serviceIntent) }

        val contentPendingIntent = getActivity(
            context, 0, contentIntent, FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setColor(ResourcesCompat.getColor(context.resources, R.color.color_accent, null))
            .setContentTitle("Будильник!")
            .setContentText("Нажмите, чтобы выключить")
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
    }

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onLowMemory() {}

    private companion object {
        private const val CHANNEL_ID = "com.duwna.alarmable.alarms"
        private const val CHANNEL_NAME = "alarms"
    }
}