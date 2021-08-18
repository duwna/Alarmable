package com.duwna.alarmable.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.duwna.alarmable.utils.log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, receiverIntent: Intent) {

        log("onReceive")

        val serviceIntent = Intent(context, BackgroundSoundService::class.java).apply {
//            putExtra("melody", intent.extras?.getString("melody"))
            putExtras(receiverIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(serviceIntent)
        else context.startService(receiverIntent)
    }
}