package com.duwna.alarmable.utils

import com.duwna.alarmable.database.Alarm
import java.util.*

object MockGenerator {

    fun generateAlarmList(count: Int) = mutableListOf<Alarm>().apply {
        repeat(count) {
            add(generateAlarm())
        }
    }

    fun generateAlarm(): Alarm {
        val r = Random()
        return Alarm(
            0,
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