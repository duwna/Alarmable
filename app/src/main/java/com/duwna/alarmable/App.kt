package com.duwna.alarmable

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.duwna.alarmable.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    alarmsModule, infoModule, taskModule, chooseCityModule,
                    networkModule, dbModule, preferencesModule
                )
            )
        }
    }
}