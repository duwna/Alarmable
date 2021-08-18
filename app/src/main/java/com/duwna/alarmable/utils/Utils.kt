package com.duwna.alarmable.utils

import android.net.Uri
import android.util.Log
import com.duwna.alarmable.data.database.alarm.Alarm
import java.text.SimpleDateFormat
import java.util.*

fun Date.format(pattern: String = "HH:mm dd.MM"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Any.log(msg: Any?, tag: String = this::class.java.simpleName) {
    Log.e(tag, msg.toString())
}

fun getTimeString(hour: Int, minute: Int): String = buildString {
    if (hour < 10) append("0"); append("$hour:")
    if (minute < 10) append("0"); append(minute)
}

fun isNextDay(hour: Int, minute: Int): Boolean {
    val calendar = Calendar.getInstance()
    val timeInMinutes = hour * 60 + minute
    val currentTimeMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    return timeInMinutes <= currentTimeMinutes
}

fun Alarm.buildDaysString() = buildString {
    if (onMon) append("ПН, ")
    if (onTue) append("ВТ, ")
    if (onWed) append("СР, ")
    if (onThu) append("ЧТ, ")
    if (onFri) append("ПТ, ")
    if (onSat) append("СБ, ")
    if (onSun) append("ВС, ")
    if (isNotEmpty()) delete(length - 2, length - 1)
}

fun Alarm.isRepeatingOnAnyDay(): Boolean {
    return onMon or onTue or onWed or onThu or onFri or onSat or onSun
}

fun Alarm.setRepeatingOnAllDays(): Alarm {
    return copy(
        isRepeating = true, onMon = true, onTue = true, onWed = true,
        onThu = true, onFri = true, onSat = true, onSun = true
    )
}

fun String?.uriOrNull(): Uri? {
    this ?: return null
    return try {
        Uri.parse(this)
    } catch (t: Throwable) {
        null
    }
}