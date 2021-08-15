package com.duwna.alarmable.database.alarm

import androidx.lifecycle.LiveData
import androidx.room.*
import com.duwna.alarmable.database.alarm.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Insert
    suspend fun insert(alarm: Alarm)

    @Query("SELECT * FROM alarm ORDER BY id DESC")
    fun getAll(): LiveData<List<Alarm>>

    @Update
    suspend fun update(alarm: Alarm)

    @Delete
    suspend fun delete(alarm: Alarm)

    @Query("SELECT * FROM alarm ORDER BY id DESC")
    fun alarmsFlow(): Flow<List<Alarm>>
}