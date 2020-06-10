package com.duwna.alarmable.api

import com.duwna.alarmable.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/weather")
    fun getWeatherByCoordsAsync(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("appid") apiKey: String
    ): Deferred<Response<WeatherResponse>>

    companion object {

        fun getApi(): WeatherService = Retrofit.Builder()
            .baseUrl(BuildConfig.OWM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(WeatherService::class.java)
    }
}