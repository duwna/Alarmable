package com.duwna.alarmable

import android.app.Application
import androidx.room.Room
import com.duwna.alarmable.database.AlarmDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AlarmDatabase::class.java, "database")
            .build()
    }

    companion object {
        lateinit var database: AlarmDatabase
    }
}