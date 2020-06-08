package com.duwna.alarmable.entities

data class Alarm(
    val id: Int,
    val time: Int,
    val isActive: Boolean,
    val melody: String,
    val hasTask: Boolean,
    val onMon: Boolean,
    val onTue: Boolean,
    val onWed: Boolean,
    val onThu: Boolean,
    val onFri: Boolean,
    val onSat: Boolean,
    val onSun: Boolean
)


