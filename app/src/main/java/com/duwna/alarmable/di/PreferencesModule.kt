package com.duwna.alarmable.di

import android.content.Context
import com.duwna.alarmable.data.PrefManager
import org.koin.dsl.module

val preferencesModule = module {
    single { providePrefManager(get()) }
}

private fun providePrefManager(context: Context): PrefManager {
    return PrefManager(context)
}