package com.duwna.alarmable.viewmodels

import androidx.lifecycle.viewModelScope
import com.duwna.alarmable.repositories.TaskRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TaskViewModel(private val repository: TaskRepository) : BaseViewModel() {

    private val difficulty = MutableStateFlow(getDifficulty())
    val answers = MutableStateFlow(generateAnswers())
    val question = MutableStateFlow(generateQuestion())
    val count = MutableStateFlow(0 to TASKS_COUNT)

    private val readyEventChannel = Channel<Unit>(Channel.BUFFERED)
    val readyEventFlow = readyEventChannel.receiveAsFlow()

    private val answersRange
        get() = (difficulty.value - difficulty.value / 2)..(difficulty.value + difficulty.value / 2)

    fun checkAnswer(position: Int) {
        if (answers.value[position] == question.value.first + question.value.second) {
            count.value = count.value.first + 1 to count.value.second
            if (count.value.first == count.value.second) {
                viewModelScope.launch { readyEventChannel.send(Unit) }
            }
        } else {
            showSnackbar(SnackBarEvent.Error("Неверно, +2 задачи!"))
            count.value = count.value.first to count.value.second + 2
        }
        answers.value = generateAnswers()
        question.value = generateQuestion()
    }

    fun applyDifficulty(progress: Int) {
        launchSafety {
            difficulty.value = progress * 10
            answers.value = generateAnswers()
            question.value = generateQuestion()
        }
    }

    private fun generateAnswers(): IntArray {
        val answers = mutableSetOf<Int>().apply {
            while (size < 4) add(answersRange.random())
        }
        return answers.toIntArray()
    }

    private fun generateQuestion(): Pair<Int, Int> {
        val rightAnswer = answers.value[(0..3).random()]
        val firstNum = answersRange.random() / 2
        val secondNum = rightAnswer - firstNum
        return firstNum to secondNum
    }

    fun saveDifficulty() = launchSafety { repository.saveDifficulty(difficulty.value) }

    fun getDifficulty() = runBlocking { repository.getDifficulty() }

    private companion object {
        const val TASKS_COUNT = 3
    }
}