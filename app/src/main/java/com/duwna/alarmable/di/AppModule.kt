package com.duwna.alarmable.di

import com.duwna.alarmable.api.WeatherService
import com.duwna.alarmable.database.alarm.AlarmDao
import com.duwna.alarmable.database.recipe.RecipeDao
import com.duwna.alarmable.repositories.InfoRepository
import com.duwna.alarmable.repositories.MainRepository
import com.duwna.alarmable.viewmodels.InfoViewModel
import com.duwna.alarmable.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { provideMainRepository(get()) }
    viewModel { MainViewModel(get()) }

    single { provideInfoRepository(get(), get()) }
    viewModel { InfoViewModel(get()) }
}

private fun provideMainRepository(alarmDao: AlarmDao): MainRepository {
    return MainRepository(alarmDao)
}

private fun provideInfoRepository(api: WeatherService, recipeDao: RecipeDao): InfoRepository {
    return InfoRepository(api, recipeDao)
}