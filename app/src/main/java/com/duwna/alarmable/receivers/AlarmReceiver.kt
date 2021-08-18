package com.duwna.alarmable.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.duwna.alarmable.services.AlarmService
import com.duwna.alarmable.utils.log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, receiverIntent: Intent) {

        log("AlarmReceiver: onReceive()")

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtras(receiverIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(serviceIntent)
        else context.startService(receiverIntent)
    }
}