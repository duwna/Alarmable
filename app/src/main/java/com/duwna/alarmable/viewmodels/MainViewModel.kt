package com.duwna.alarmable.viewmodels

import androidx.lifecycle.MutableLiveData
import com.duwna.alarmable.App
import com.duwna.alarmable.database.Alarm
import com.duwna.alarmable.ui.AlarmClickListener
import com.duwna.alarmable.utils.log

class MainViewModel : BaseViewModel() {

    private val dao = App.database.alarmDao()

    val alarms = MutableLiveData<List<Alarm>>()

    init {
        log("INIT")
        loadAlarms()
    }

    fun addAlarm(hourOfDay: Int, minute: Int) {
        doAsync {
            val alarm = Alarm(
                0, hourOfDay * 60 + minute, true, "По умолчанию", false
            )
            dao.insert(alarm)
            alarms.postValue(dao.getAll().also { log(it.map { a -> a.id }) })
        }
    }

    fun setListeners() = AlarmClickListener(
        onSwitchClicked = { updateValue(it.copy(isActive = !it.isActive)) },
        onMelodyClicked = { },
        onTaskClicked = { updateValue(it.copy(hasTask = !it.hasTask)) },
        onDeleteClicked = { delete(it) },
        onMonClicked = { updateValue(it.copy(onMon = !it.onMon)) },
        onTueClicked = { updateValue(it.copy(onTue = !it.onTue)) },
        onWedClicked = { updateValue(it.copy(onWed = !it.onWed)) },
        onThuClicked = { updateValue(it.copy(onThu = !it.onThu)) },
        onFriClicked = { updateValue(it.copy(onFri = !it.onFri)) },
        onSatClicked = { updateValue(it.copy(onSat = !it.onSat)) },
        onSunClicked = { updateValue(it.copy(onSun = !it.onSun)) }
    )

    private fun delete(alarm: Alarm) = doAsync {
        dao.delete(alarm)
        alarms.postValue(dao.getAll())
    }

    private fun updateValue(alarm: Alarm) = doAsync {
        dao.update(alarm)
        alarms.postValue(dao.getAll())
    }

    private fun loadAlarms() {
        doAsync { alarms.postValue(dao.getAll()) }
    }
}

