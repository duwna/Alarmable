package com.duwna.alarmable.utils

import com.duwna.alarmable.entities.Alarm
import java.util.*

object MockGenerator {

    fun generateAlarmList(count: Int) = mutableListOf<Alarm>().apply {
        repeat(count) {
            add(generateAlarm())
        }
    }

    private fun generateAlarm(): Alarm {
        val r = Random()
        return Alarm(
            r.nextInt(),
            (0..1440).random(),
            r.nextBoolean(),
            "По умолчанию",
            r.nextBoolean(),
            r.nextBoolean(),
            r.nextBoolean(),
            r.nextBoolean(),
            r.nextBoolean(),
            r.nextBoolean(),
            r.nextBoolean(),
            r.nextBoolean()
        )
    }
}