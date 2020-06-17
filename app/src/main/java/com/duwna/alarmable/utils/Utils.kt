package com.duwna.alarmable.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

fun Date.format(pattern: String = "HH:mm dd.MM"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Any.log(msg: Any?, tag: String = this::class.java.simpleName) {
    Log.e(tag, msg.toString())
}

fun String.toInitials(): String = when {
    length >= 2 -> substring(0, 2).toUpperCase(Locale.US)
    isNotBlank() -> substring(0, 1).toUpperCase(Locale.US)
    else -> ""
}


fun Double.format(digits: Int = 2) = "%.${digits}f".format(Locale.US, this)

fun Double.equalsDelta(other: Double) = kotlin.math.abs(this / other - 1) < 0.01

fun Int.toTimeString(): String {
    val hours = this / 60
    val minutes = this - hours * 60
    val hoursStr = if (hours < 10) "0$hours" else "$hours"
    val minutesStr = if (minutes < 10) "0$minutes" else "$minutes"
    return "$hoursStr:$minutesStr"
}

fun Int.toDayString(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return if (hour * 60 + minute > this) "Завтра"
    else "Сегодня"
}

fun buildDaysString(
    onMon: Boolean,
    onTue: Boolean,
    onWed: Boolean,
    onThu: Boolean,
    onFri: Boolean,
    onSat: Boolean,
    onSun: Boolean
): String = buildString {
    if (onMon) append("ПН, ")
    if (onTue) append("ВТ, ")
    if (onWed) append("СР, ")
    if (onThu) append("ЧТ, ")
    if (onFri) append("ПТ, ")
    if (onSat) append("СБ, ")
    if (onSun) append("ВС, ")
    if (isNotEmpty()) delete(length - 2, length - 1)
}