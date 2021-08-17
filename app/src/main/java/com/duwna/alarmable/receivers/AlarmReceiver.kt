package com.duwna.alarmable.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.duwna.alarmable.utils.log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
//        context?.startActivity(
//            Intent(
//                context.applicationContext,
//                TaskActivity::class.java
//            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })

        log("onReceive")
    }
}