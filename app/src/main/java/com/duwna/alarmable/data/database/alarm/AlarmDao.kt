package com.duwna.alarmable.data.database.alarm

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarm ORDER BY id DESC")
    fun getAll(): Flow<List<Alarm>>

    @Insert
    suspend fun insert(alarm: Alarm): Long

    @Update
    suspend fun update(alarm: Alarm)

    @Delete
    suspend fun delete(alarm: Alarm)

}