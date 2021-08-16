package com.duwna.alarmable.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.duwna.alarmable.database.alarm.Alarm
import com.duwna.alarmable.repositories.MainRepository
import com.duwna.alarmable.ui.adapters.AlarmClickListener
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(private val repository: MainRepository) : BaseViewModel() {

    val alarms = repository.alarmsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // while choosing melody
    val tempAlarm = MutableLiveData<Alarm>()

    fun addAlarm(hourOfDay: Int, minute: Int) = launchSafety {
        val alarm = Alarm(
            0, hourOfDay * 60 + minute, true, "По умолчанию", false
        )
        repository.insert(alarm)
    }


    fun setListeners() = AlarmClickListener(
        onSwitchClicked = { updateAlarm(it.copy(isActive = !it.isActive)) },
        onTaskClicked = { updateAlarm(it.copy(hasTask = !it.hasTask)) },
        onDeleteClicked = { deleteAlarm(it) },
        onRepeatClicked = { updateAlarm(it.copy(isRepeating = !it.isRepeating)) },
        onMonClicked = { updateAlarm(it.copy(onMon = !it.onMon)) },
        onTueClicked = { updateAlarm(it.copy(onTue = !it.onTue)) },
        onWedClicked = { updateAlarm(it.copy(onWed = !it.onWed)) },
        onThuClicked = { updateAlarm(it.copy(onThu = !it.onThu)) },
        onFriClicked = { updateAlarm(it.copy(onFri = !it.onFri)) },
        onSatClicked = { updateAlarm(it.copy(onSat = !it.onSat)) },
        onSunClicked = { updateAlarm(it.copy(onSun = !it.onSun)) }
    )

    fun setMelody(name: String) {
        if (tempAlarm.value != null) {
            updateAlarm(tempAlarm.value!!.copy(melody = name))
        }
    }

    private fun deleteAlarm(alarm: Alarm) = launchSafety {
        repository.delete(alarm)
    }

    private fun updateAlarm(alarm: Alarm) = launchSafety {
        repository.update(alarm)
    }
}

