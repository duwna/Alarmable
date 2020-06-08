package com.duwna.alarmable.database

import androidx.room.*

@Dao
interface AlarmDao {

    @Insert
    suspend fun insert(alarm: Alarm)

    @Query("SELECT * FROM alarm ORDER BY id DESC")
    suspend fun getAll(): List<Alarm>

    @Update
    suspend fun update(alarm: Alarm)

    @Delete
    suspend fun delete(alarm: Alarm)
}