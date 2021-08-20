package com.duwna.alarmable.viewmodels

import androidx.lifecycle.MutableLiveData
import com.duwna.alarmable.data.api.WeatherResponse
import com.duwna.alarmable.data.database.alarm.Alarm
import com.duwna.alarmable.data.database.recipe.Recipe
import com.duwna.alarmable.repositories.InfoRepository
import com.duwna.alarmable.utils.MockGenerator

class InfoViewModel(private val repository: InfoRepository) : BaseViewModel() {

    val weather = MutableLiveData<WeatherResponse?>(null)
    val recipe = MutableLiveData<Recipe>()

    init {
        loadRecipe()
    }

    fun loadWeather(lat: Double, lon: Double) = launchSafety {

        val weatherResponse = repository
            .getWeatherByCoords(lat, lon)

        weather.postValue(weatherResponse)
    }

    fun loadRecipe() = launchSafety {
        if (repository.getRandom() == null) prePopulate()
        recipe.postValue(repository.getRandom())
    }

    private suspend fun prePopulate() {
        MockGenerator.recipes.forEach { repository.insert(it) }
    }

    fun cancelAlarm(alarm: Alarm) = launchSafety {
        repository.cancelAlarmInDb(alarm)
    }
}