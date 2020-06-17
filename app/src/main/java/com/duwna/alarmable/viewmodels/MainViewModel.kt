package com.duwna.alarmable.viewmodels

import androidx.lifecycle.MutableLiveData
import com.duwna.alarmable.App
import com.duwna.alarmable.database.alarm.Alarm
import com.duwna.alarmable.ui.adapters.AlarmClickListener

class MainViewModel : BaseViewModel() {

    private val dao = App.database.alarmDao()

    val alarms = MutableLiveData<List<Alarm>>()

    init {
        runAsync { loadAlarms() }
    }

    fun addAlarm(hourOfDay: Int, minute: Int) = runAsync {
        val alarm = Alarm(
            0, hourOfDay * 60 + minute, true, "По умолчанию", false
        )
        dao.insert(alarm)
        loadAlarms()
    }


    fun setListeners() = AlarmClickListener(
        onSwitchClicked = { updateAlarm(it.copy(isActive = !it.isActive)) },
        onMelodyClicked = { },
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

    private fun deleteAlarm(alarm: Alarm) = runAsync {
        dao.delete(alarm)
        loadAlarms()
    }

    private fun updateAlarm(alarm: Alarm) = runAsync {
        dao.update(alarm)
        loadAlarms()
    }

    private suspend fun loadAlarms() {
        alarms.postValue(dao.getAll())
    }
}

