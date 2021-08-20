package com.duwna.alarmable.repositories

import android.annotation.SuppressLint
import android.location.Location
import com.duwna.alarmable.data.PrefManager
import com.duwna.alarmable.data.api.City
import com.duwna.alarmable.data.api.WeatherService
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class ChooseCityRepository(
    private val api: WeatherService,
    private val locationProvider: FusedLocationProviderClient,
    private val prefs: PrefManager
) {

    suspend fun searchByName(name: String): City {
        return api.getCityByName(name)
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): City {
        val location = awaitLastLocation()
        return api.getCityByCoords(location.latitude, location.longitude)
    }

    @SuppressLint("MissingPermission")
    private suspend fun awaitLastLocation(): Location =
        suspendCancellableCoroutine { continuation ->
            locationProvider.lastLocation.addOnSuccessListener { location ->
                continuation.resume(location, null)
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
        }

    suspend fun save(city: City) = prefs.saveLocation(city)
}
