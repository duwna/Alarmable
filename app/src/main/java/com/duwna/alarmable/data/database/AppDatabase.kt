package com.duwna.alarmable.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.duwna.alarmable.BuildConfig
import com.duwna.alarmable.data.database.AppDatabase.Companion.DATABASE_VERSION
import com.duwna.alarmable.data.database.alarm.Alarm
import com.duwna.alarmable.data.database.alarm.AlarmDao
import com.duwna.alarmable.data.database.recipe.Recipe
import com.duwna.alarmable.data.database.recipe.RecipeDao

@Database(version = DATABASE_VERSION, entities = [Alarm::class, Recipe::class])
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 1
    }

    abstract fun alarmDao(): AlarmDao
    abstract fun recipeDao(): RecipeDao
}