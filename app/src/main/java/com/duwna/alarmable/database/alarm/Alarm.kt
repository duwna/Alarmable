package com.duwna.alarmable.database.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val time: Int,
    val isActive: Boolean,
    val melody: String,
    val hasTask: Boolean,
    val isRepeating: Boolean = false,
    val onMon: Boolean = false,
    val onTue: Boolean = false,
    val onWed: Boolean = false,
    val onThu: Boolean = false,
    val onFri: Boolean = false,
    val onSat: Boolean = false,
    val onSun: Boolean = false
)

























