package com.duwna.alarmable.di

import com.duwna.alarmable.data.api.WeatherService
import com.duwna.alarmable.data.database.alarm.AlarmDao
import com.duwna.alarmable.data.database.recipe.RecipeDao
import com.duwna.alarmable.repositories.InfoRepository
import com.duwna.alarmable.viewmodels.InfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val infoModule = module {
    single { provideInfoRepository(get(), get(), get()) }
    viewModel { InfoViewModel(get()) }
}

private fun provideInfoRepository(
    api: WeatherService,
    recipeDao: RecipeDao,
    alarmDao: AlarmDao
): InfoRepository {
    return InfoRepository(api, recipeDao, alarmDao)
}
