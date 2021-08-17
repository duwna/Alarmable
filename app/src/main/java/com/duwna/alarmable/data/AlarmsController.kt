package com.duwna.alarmable.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.duwna.alarmable.receivers.AlarmReceiver
import com.duwna.alarmable.utils.isNextDay
import com.duwna.alarmable.utils.log
import java.util.*

class AlarmsController(
    private val context: Context,
    private val alarmManager: AlarmManager
) {

    fun setAlarm(hour: Int, minute: Int, alarmId: Int) {

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

//        for testing
//        val triggerTime = Calendar.getInstance().apply {
//            add(Calendar.SECOND, 10)
//        }.timeInMillis

        val triggerTime = getTriggerTimeMillis(hour, minute)

        log(Date(triggerTime))

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    fun cancelAlarm(alarmId: Int) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun getTriggerTimeMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            // add one day to trigger time if time in arguments is in the next day
            if (isNextDay(hour, minute)) add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        return calendar.timeInMillis
    }
}