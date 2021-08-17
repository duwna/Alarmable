package com.duwna.alarmable.repositories

import com.duwna.alarmable.data.AlarmsController
import com.duwna.alarmable.data.database.alarm.Alarm
import com.duwna.alarmable.data.database.alarm.AlarmDao
import kotlinx.coroutines.flow.Flow

class MainRepository(
    private val alarmsDao: AlarmDao,
    private val alarmsController: AlarmsController
) {

    fun alarmsFlow(): Flow<List<Alarm>> = alarmsDao.getAll()

    suspend fun createAlarm(hour: Int, minute: Int) {

        val alarm = Alarm(
            id = 0,
            hour = hour,
            minute = minute,
            isActive = true,
            melody = null,
            hasTask = false,
            isRepeating = false,
            onMon = false,
            onTue = false,
            onWed = false,
            onThu = false,
            onFri = false,
            onSat = false,
            onSun = false
        )

        val alarmId = alarmsDao.insert(alarm)
        alarmsController.setAlarm(hour, minute, alarmId.toInt())
    }

    suspend fun update(newAlarm: Alarm) {
        alarmsDao.update(newAlarm)

        if (newAlarm.isActive) {
//            val hour =
//            createAlarm()
        }
    }

    suspend fun delete(alarm: Alarm) = alarmsDao.delete(alarm)
}

