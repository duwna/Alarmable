package com.duwna.alarmable.viewmodels

import androidx.lifecycle.MutableLiveData
import com.duwna.alarmable.App
import com.duwna.alarmable.BuildConfig
import com.duwna.alarmable.api.WeatherResponse
import com.duwna.alarmable.api.WeatherService
import com.duwna.alarmable.database.recipe.Recipe
import com.duwna.alarmable.database.recipe.RecipeDao
import com.duwna.alarmable.repositories.InfoRepository
import com.duwna.alarmable.utils.MockGenerator

class InfoViewModel(private  val repository: InfoRepository) : BaseViewModel() {

    val weather = MutableLiveData<WeatherResponse?>(null)
    val recipe = MutableLiveData<Recipe>()

    init {
        loadRecipe()
    }

    fun loadWeather(lat: Double, lon: Double) = launchSafety {

        val weatherResponse = repository
            .getWeatherByCoordsAsync(lat, lon, "ru")

        weather.postValue(weatherResponse)
    }

    fun loadRecipe() = launchSafety {
        if (repository.getRandom() == null) prePopulate()
        recipe.postValue(repository.getRandom())
    }

    private suspend fun prePopulate() {
        MockGenerator.recipes.forEach { repository.insert(it) }
    }

}