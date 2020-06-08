package com.duwna.alarmable.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Alarm::class])
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

}