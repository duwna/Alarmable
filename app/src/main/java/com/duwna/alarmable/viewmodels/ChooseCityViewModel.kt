package com.duwna.alarmable.viewmodels

import androidx.lifecycle.viewModelScope
import com.duwna.alarmable.data.api.City
import com.duwna.alarmable.repositories.ChooseCityRepository
import com.duwna.alarmable.utils.log
import com.duwna.alarmable.utils.tryOrNull
import kotlinx.coroutines.flow.*

class ChooseCityViewModel(
    private val repository: ChooseCityRepository
) : BaseViewModel() {

    val currentLocation = MutableStateFlow<LocationState>(LocationState.Empty)
    val searchedLocation = MutableStateFlow<LocationState>(LocationState.Empty)
    val isSearchMode = MutableStateFlow(false)

    fun getCurrentLocation() = launchSafety {
        val response = tryOrNull { repository.getCurrentLocation() }
        currentLocation.value = if (response != null) LocationState.Success(response)
        else LocationState.Error("Не удалось определить местоположение...")
    }

    fun subscribeOnSearchQuery(queryFlow: Flow<String>) {
        queryFlow.debounce(500).onEach { cityName ->
            searchedLocation.value = when {
                cityName.isBlank() -> LocationState.Empty
                else -> {
                    val response = tryOrNull { repository.searchByName(cityName) }
                    if (response != null) LocationState.Success(response)
                    else LocationState.Error("Город не найден: $cityName")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun toggleSearchMode() {
        isSearchMode.value = !isSearchMode.value
    }

    fun saveLocation() = launchSafety {
        when {
            isSearchMode.value && searchedLocation.value is LocationState.Success -> {
                repository.save((searchedLocation.value as LocationState.Success).city.also { log(it) })
            }
            !isSearchMode.value && currentLocation.value is LocationState.Success -> {
                repository.save((currentLocation.value as LocationState.Success).city.also { log(it) })
            }
        }
    }

    sealed class LocationState {
        object Empty : LocationState()
        data class Success(val city: City) : LocationState()
        data class Error(val message: String) : LocationState()
    }

}