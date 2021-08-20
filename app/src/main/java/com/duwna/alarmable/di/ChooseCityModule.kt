package com.duwna.alarmable.di

import android.content.Context
import com.duwna.alarmable.data.PrefManager
import com.duwna.alarmable.data.api.WeatherService
import com.duwna.alarmable.repositories.ChooseCityRepository
import com.duwna.alarmable.viewmodels.ChooseCityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val chooseCityModule = module {
    single { provideLocationProvider(get()) }
    single { provideChooseCityRepository(get(), get(), get()) }
    viewModel { ChooseCityViewModel(get()) }
}

private fun provideChooseCityRepository(
    api: WeatherService,
    locationProvider: FusedLocationProviderClient,
    prefs: PrefManager
): ChooseCityRepository {
    return ChooseCityRepository(api, locationProvider, prefs)
}

private fun provideLocationProvider(context: Context): FusedLocationProviderClient {
    return LocationServices.getFusedLocationProviderClient(context)
}