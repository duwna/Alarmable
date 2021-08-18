package com.duwna.alarmable.receivers

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.duwna.alarmable.R
import com.duwna.alarmable.ui.TaskActivity
import com.duwna.alarmable.utils.uriOrNull

class BackgroundSoundService : Service() {

    private lateinit var player: MediaPlayer

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationManager = NotificationManagerCompat.from(this)

        createNotificationChannel(notificationManager)
        startForeground(1, createNotification(this))

        val melodyUri = intent?.extras?.getString("melody").uriOrNull()
        player = if (melodyUri != null) MediaPlayer.create(this, melodyUri)
        else MediaPlayer.create(this, R.raw.alarm)

        player.apply {
            isLooping = true
            setVolume(100f, 100f)
            start()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        player.stop()
        player.release()
    }

    override fun onLowMemory() {}

    private fun createNotificationChannel(notificationManager: NotificationManagerCompat) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(context: Context): Notification {

        val contentIntent = Intent(context, TaskActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(context, 0, contentIntent, 0)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_add_alarm)
            .setColor(ResourcesCompat.getColor(context.resources, R.color.color_accent, null))
            .setContentTitle("Будильник!")
            .setContentText("Нажмите, чтобы выключить")
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "com.duwna.alarmable.alarms"
        private const val CHANNEL_NAME = "alarms"
    }
}