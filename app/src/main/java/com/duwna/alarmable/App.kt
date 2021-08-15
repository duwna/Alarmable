package com.duwna.alarmable

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import com.duwna.alarmable.database.AlarmDatabase
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        database = Room.databaseBuilder(this, AlarmDatabase::class.java, "AlarmDatabase")
            .build()

        startKoin {

        }
    }

    companion object {
        lateinit var database: AlarmDatabase
    }
}