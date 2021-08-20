package com.duwna.alarmable.viewmodels

import com.duwna.alarmable.data.api.WeatherResponse
import com.duwna.alarmable.data.database.alarm.Alarm
import com.duwna.alarmable.data.database.recipe.Recipe
import com.duwna.alarmable.repositories.InfoRepository
import com.duwna.alarmable.utils.MockGenerator
import kotlinx.coroutines.flow.MutableStateFlow

class InfoViewModel(private val repository: InfoRepository) : BaseViewModel() {

    val weather = MutableStateFlow<WeatherResponse?>(null)
    val recipe = MutableStateFlow<Recipe?>(null)

    init {
        loadRecipe()
        loadWeather()
    }

    fun loadRecipe() = launchSafety {
        if (repository.getRandom() == null) prePopulate()
        recipe.value = repository.getRandom()
    }

    private fun loadWeather() = launchSafety {
        weather.value = repository.loadWeather()
    }

    private suspend fun prePopulate() {
        MockGenerator.recipes.forEach { repository.insert(it) }
    }

    fun cancelAlarm(alarm: Alarm) = launchSafety {
        repository.cancelAlarmInDb(alarm)
    }
}