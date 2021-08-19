package com.duwna.alarmable.repositories

import com.duwna.alarmable.data.api.WeatherResponse
import com.duwna.alarmable.data.api.WeatherService
import com.duwna.alarmable.data.database.alarm.Alarm
import com.duwna.alarmable.data.database.alarm.AlarmDao
import com.duwna.alarmable.data.database.recipe.Recipe
import com.duwna.alarmable.data.database.recipe.RecipeDao

class InfoRepository(
    private val api: WeatherService,
    private val recipeDao: RecipeDao,
    private val alarmDao: AlarmDao
) {

    suspend fun getWeatherByCoordsAsync(
        lat: Double,
        lon: Double,
        query: String,
    ): WeatherResponse = api.getWeatherByCoordsAsync(lat, lon, query)

    suspend fun getRandom() = recipeDao.getRandom()

    suspend fun insert(recipe: Recipe) = recipeDao.insert(recipe)

    suspend fun cancelAlarmInDb(alarm: Alarm) {
        alarmDao.update(alarm.copy(isActive = false))
    }
}