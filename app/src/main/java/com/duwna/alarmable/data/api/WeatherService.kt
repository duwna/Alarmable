package com.duwna.alarmable.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeatherByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ): WeatherResponse

    @GET("data/2.5/weather")
    suspend fun getCityByName(
        @Query("q") name: String,
    ): City

    @GET("data/2.5/weather")
    suspend fun getCityByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ): City
}