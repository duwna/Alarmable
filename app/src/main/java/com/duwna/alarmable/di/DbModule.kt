package com.duwna.alarmable.di

import android.content.Context
import androidx.room.Room
import com.duwna.alarmable.data.PrefManager
import com.duwna.alarmable.data.database.AppDatabase
import com.duwna.alarmable.data.database.alarm.AlarmDao
import com.duwna.alarmable.data.database.recipe.RecipeDao
import org.koin.dsl.module

val dbModule = module {
    single { provideDatabase(get()) }
    single { provideAlarmDao(get()) }
    single { provideRecipeDao(get()) }
}

private fun provideDatabase(context: Context): AppDatabase = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    AppDatabase.DATABASE_NAME
).fallbackToDestructiveMigration().build()

private fun provideAlarmDao(appDatabase: AppDatabase): AlarmDao = appDatabase.alarmDao()

private fun provideRecipeDao(appDatabase: AppDatabase): RecipeDao = appDatabase.recipeDao()