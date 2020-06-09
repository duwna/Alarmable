package com.duwna.alarmable.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.duwna.alarmable.R
import com.google.android.material.snackbar.Snackbar
import com.robinhood.ticker.TickerUtils
import kotlinx.android.synthetic.main.activity_task.*

class TaskActivity : AppCompatActivity() {

    private val maxNumber = 200

    private var tasksCount = 3
    private var tasksReady = 0

    private var answers = BooleanArray(4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        setupViews()
        resumeQuiz()
    }

    private fun resumeQuiz() {
        if (tasksCount != tasksReady) {

            tv_count.text = "$tasksReady/$tasksCount"

            val firstNum = (0..maxNumber).random()
            val secondNum = (0..maxNumber).random()
            val sum = firstNum + secondNum

            answers.indices.forEach { answers[it] = false }
            answers[(0..3).random()] = true

            val deltaRange = (-maxNumber / 2)..(maxNumber / 2)

            tv_0.text = if (answers[0]) sum.toString() else (sum + (deltaRange).random()).toString()
            tv_1.text = if (answers[1]) sum.toString() else (sum + (deltaRange).random()).toString()
            tv_2.text = if (answers[2]) sum.toString() else (sum + (deltaRange).random()).toString()
            tv_3.text = if (answers[3]) sum.toString() else (sum + (deltaRange).random()).toString()

            tv_task.text = "$firstNum + $secondNum"
        } else {
            finish()
        }
    }

    private fun checkAnswer(isRight: Boolean) {
        tasksReady++
        if (!isRight) {
            tasksCount += 2
            Snackbar.make(container, "Неверно, +2 задачи!", Snackbar.LENGTH_SHORT).apply {
                setBackgroundTint(getColor(R.color.design_default_color_error))
                setTextColor(getColor(android.R.color.white))
                show()
            }
        }
        resumeQuiz()
    }

    private fun setupViews() {

        tv_count.setCharacterLists(TickerUtils.provideNumberList())
        tv_task.setCharacterLists(TickerUtils.provideNumberList())
        tv_0.setCharacterLists(TickerUtils.provideNumberList())
        tv_1.setCharacterLists(TickerUtils.provideNumberList())
        tv_2.setCharacterLists(TickerUtils.provideNumberList())
        tv_3.setCharacterLists(TickerUtils.provideNumberList())

        btn_0.setOnClickListener {
            checkAnswer(answers[0])
        }

        btn_1.setOnClickListener {
            checkAnswer(answers[1])
        }

        btn_2.setOnClickListener {
            checkAnswer(answers[2])
        }

        btn_3.setOnClickListener {
            checkAnswer(answers[3])
        }
    }
}

