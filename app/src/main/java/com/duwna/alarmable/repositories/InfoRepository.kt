package com.duwna.alarmable.repositories

import com.duwna.alarmable.api.WeatherResponse
import com.duwna.alarmable.api.WeatherService
import com.duwna.alarmable.database.recipe.Recipe
import com.duwna.alarmable.database.recipe.RecipeDao

class InfoRepository(private val api: WeatherService, private val recipeDao: RecipeDao) {

    suspend fun getWeatherByCoordsAsync(
        lat: Double,
        lon: Double,
        query: String,
    ): WeatherResponse = api.getWeatherByCoordsAsync(lat, lon, query)

    suspend fun getRandom() = recipeDao.getRandom()

    suspend fun insert(recipe: Recipe) = recipeDao.insert(recipe)
}