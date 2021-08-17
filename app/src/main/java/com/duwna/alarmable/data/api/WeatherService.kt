package com.duwna.alarmable.data.api

import com.duwna.alarmable.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeatherByCoordsAsync(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("appid") apiKey: String = BuildConfig.OWM_API_KEY
    ): WeatherResponse
}