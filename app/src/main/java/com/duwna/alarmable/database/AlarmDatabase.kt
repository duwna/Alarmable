package com.duwna.alarmable.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.duwna.alarmable.database.alarm.Alarm
import com.duwna.alarmable.database.alarm.AlarmDao
import com.duwna.alarmable.database.recipe.Recipe
import com.duwna.alarmable.database.recipe.RecipeDao

@Database(version = 1, entities = [Alarm::class, Recipe::class])
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

    abstract fun recipeDao(): RecipeDao

}