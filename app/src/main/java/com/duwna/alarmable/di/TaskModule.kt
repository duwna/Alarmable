package com.duwna.alarmable.di

import com.duwna.alarmable.data.PrefManager
import com.duwna.alarmable.repositories.TaskRepository
import com.duwna.alarmable.viewmodels.TaskViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val taskModule = module {
    single { provideTaskRepository(get()) }
    viewModel { TaskViewModel(get()) }
}

private fun provideTaskRepository(prefs: PrefManager): TaskRepository = TaskRepository(prefs)
