package com.duwna.alarmable.di

import com.duwna.alarmable.BuildConfig
import com.duwna.alarmable.api.WeatherService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { provideRetrofit() }
    single { provideWeatherService(get()) }
}

private fun provideRetrofit(): Retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.OWM_BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private fun provideWeatherService(retrofit: Retrofit): WeatherService {
    return retrofit.create(WeatherService::class.java)
}