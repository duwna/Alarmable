package com.duwna.alarmable.repositories

import com.duwna.alarmable.database.alarm.Alarm
import com.duwna.alarmable.database.alarm.AlarmDao
import kotlinx.coroutines.flow.Flow

class MainRepository(private val alarmDao: AlarmDao) {
    fun alarmsFlow(): Flow<List<Alarm>> = alarmDao.alarmsFlow()
    suspend fun insert(alarm: Alarm) = alarmDao.insert(alarm)
    suspend fun update(alarm: Alarm) = alarmDao.update(alarm)
    suspend fun delete(alarm: Alarm) = alarmDao.delete(alarm)
}