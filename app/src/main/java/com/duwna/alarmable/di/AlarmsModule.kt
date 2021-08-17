package com.duwna.alarmable.di

import android.app.AlarmManager
import android.content.Context
import com.duwna.alarmable.data.AlarmsController
import com.duwna.alarmable.data.database.alarm.AlarmDao
import com.duwna.alarmable.repositories.MainRepository
import com.duwna.alarmable.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val alarmsModule = module {
    single { provideAlarmManager(get()) }
    single { provideAlarmsController(get(), get()) }
    single { provideMainRepository(get(), get()) }
    viewModel { MainViewModel(get()) }
}

private fun provideMainRepository(
    alarmDao: AlarmDao,
    alarmsController: AlarmsController
): MainRepository {
    return MainRepository(alarmDao, alarmsController)
}

private fun provideAlarmsController(context: Context, alarmManager: AlarmManager): AlarmsController {
    return AlarmsController(context, alarmManager)
}

private fun provideAlarmManager(context: Context): AlarmManager {
    return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}