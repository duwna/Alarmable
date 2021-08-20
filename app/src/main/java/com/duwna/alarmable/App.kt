package com.duwna.alarmable

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.duwna.alarmable.di.*
import com.duwna.alarmable.utils.log
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        startKoin {
            androidContext(this@App)
            modules(listOf(alarmsModule, infoModule, chooseCityModule, networkModule, dbModule))
        }
    }
}