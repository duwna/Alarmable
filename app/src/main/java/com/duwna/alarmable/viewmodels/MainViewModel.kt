package com.duwna.alarmable.viewmodels

import androidx.lifecycle.viewModelScope
import com.duwna.alarmable.data.database.alarm.Alarm
import com.duwna.alarmable.repositories.MainRepository
import com.duwna.alarmable.ui.adapters.AlarmClickListener
import com.duwna.alarmable.utils.isRepeatingOnAnyDay
import com.duwna.alarmable.utils.setRepeatingOnAllDays
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val repository: MainRepository,
) : BaseViewModel() {

    val alarms = repository.alarmsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // while choosing melody
    val pendingAlarm = MutableStateFlow<Alarm?>(null)

    fun onTimePicked(hour: Int, minute: Int) = launchSafety {
        repository.createAlarm(hour, minute)
    }

    fun setListeners() = AlarmClickListener(
        onDeleteClicked = { oldAlarm -> deleteAlarm(oldAlarm) },
        onRepeatClicked = { oldAlarm -> setRepeatedAlarm(oldAlarm) },
        onSwitchClicked = { oldAlarm -> setActiveAlarm(oldAlarm) },
        onTaskClicked = { updateAlarm(it.copy(hasTask = !it.hasTask)) },

        onMonClicked = { oldAlarm -> setRepeatingDay(oldAlarm.copy(onMon = !oldAlarm.onMon)) },
        onTueClicked = { oldAlarm -> setRepeatingDay(oldAlarm.copy(onTue = !oldAlarm.onTue)) },
        onWedClicked = { oldAlarm -> setRepeatingDay(oldAlarm.copy(onWed = !oldAlarm.onWed)) },
        onThuClicked = { oldAlarm -> setRepeatingDay(oldAlarm.copy(onThu = !oldAlarm.onThu)) },
        onFriClicked = { oldAlarm -> setRepeatingDay(oldAlarm.copy(onFri = !oldAlarm.onFri)) },
        onSatClicked = { oldAlarm -> setRepeatingDay(oldAlarm.copy(onSat = !oldAlarm.onSat)) },
        onSunClicked = { oldAlarm -> setRepeatingDay(oldAlarm.copy(onSun = !oldAlarm.onSun)) }
    )

    private fun setActiveAlarm(oldAlarm: Alarm) {
        //TODO implement repeating alarms
        if (oldAlarm.isRepeating && !oldAlarm.isActive) {
            updateAlarm(oldAlarm.copy(isRepeating = false, isActive = !oldAlarm.isActive))
            notify(Notify.Error("Repeating alarms are not implemented..."))
            return
        }

        updateAlarm(oldAlarm.copy(isActive = !oldAlarm.isActive))
    }

    private fun setRepeatingDay(oldAlarm: Alarm) {
        val newAlarm = when {
            !oldAlarm.isRepeatingOnAnyDay() -> oldAlarm.copy(isRepeating = false)
            else -> oldAlarm
        }
        updateAlarm(newAlarm)
    }

    private fun setRepeatedAlarm(oldAlarm: Alarm) {
        val newAlarm = when {
            !oldAlarm.isRepeating -> when {
                !oldAlarm.isRepeatingOnAnyDay() -> oldAlarm.setRepeatingOnAllDays()
                else -> oldAlarm.copy(isRepeating = true)
            }
            else -> oldAlarm.copy(isRepeating = false)
        }

        //TODO implement repeating alarms
        if (newAlarm.isRepeating) {
            updateAlarm(newAlarm.copy(isActive = false))
            notify(Notify.Error("Repeating alarms are not implemented..."))
            return
        }

        updateAlarm(newAlarm)
    }

    fun setMelody(name: String) {
        if (pendingAlarm.value != null) {
            updateAlarm(pendingAlarm.value!!.copy(melody = name))
        }
    }

    private fun deleteAlarm(alarm: Alarm) = launchSafety {
        repository.delete(alarm)
    }

    private fun updateAlarm(alarm: Alarm) = launchSafety {
        repository.update(alarm)
    }
}

