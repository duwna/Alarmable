package com.duwna.alarmable.viewmodels

import androidx.lifecycle.MutableLiveData
import com.duwna.alarmable.App
import com.duwna.alarmable.BuildConfig
import com.duwna.alarmable.api.WeatherResponse
import com.duwna.alarmable.api.WeatherService
import com.duwna.alarmable.database.recipe.Recipe
import com.duwna.alarmable.utils.MockGenerator

class InfoViewModel : BaseViewModel() {

    private val weatherApi = WeatherService.getApi()
    private val recipeDao = App.database.recipeDao()

    val weather = MutableLiveData<WeatherResponse?>(null)
    val recipe = MutableLiveData<Recipe>()

    init {
        loadRecipe()
    }

    fun loadWeather(lat: Double, lon: Double) = runAsync {

        val response = weatherApi
            .getWeatherByCoordsAsync(lat, lon, "ru", BuildConfig.OWM_API_KEY)
            .await()

        if (response.isSuccessful) {
            weather.postValue(response.body())
        } else {
            notify(Notify.Error())
        }
    }

    fun loadRecipe() = runAsync {
        if (recipeDao.getRandom() == null) prePopulate()
        recipe.postValue(recipeDao.getRandom())
    }

    private suspend fun prePopulate() {
        MockGenerator.recipes.forEach { recipeDao.insert(it) }
    }

}