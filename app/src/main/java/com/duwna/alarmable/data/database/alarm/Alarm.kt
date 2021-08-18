package com.duwna.alarmable.data.database.alarm

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val hour: Int,
    val minute: Int,
    val isActive: Boolean,
    val melodyName: String?,
    val melodyUri: String?,
    val hasTask: Boolean,
    val isRepeating: Boolean,
    val onMon: Boolean,
    val onTue: Boolean,
    val onWed: Boolean,
    val onThu: Boolean,
    val onFri: Boolean,
    val onSat: Boolean,
    val onSun: Boolean
) : Parcelable {

    companion object {
        const val KEY = "ALARM_KEY"
    }
}





















