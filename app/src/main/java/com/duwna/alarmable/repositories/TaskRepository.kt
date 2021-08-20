package com.duwna.alarmable.repositories

import com.duwna.alarmable.data.PrefManager

class TaskRepository(private val prefs: PrefManager) {
    suspend fun saveDifficulty(difficulty: Int) = prefs.saveDifficulty(difficulty)
    suspend fun getDifficulty(): Int = prefs.loadDifficulty()
}