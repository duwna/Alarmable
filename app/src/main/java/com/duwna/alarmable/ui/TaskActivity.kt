package com.duwna.alarmable.ui

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.duwna.alarmable.R
import com.duwna.alarmable.databinding.ActivityTaskBinding
import com.duwna.alarmable.utils.format
import com.google.android.material.snackbar.Snackbar
import com.robinhood.ticker.TickerUtils
import java.util.*

class TaskActivity : AppCompatActivity() {

    private val binding: ActivityTaskBinding by viewBinding()

    private val maxNumber = 200

    private var tasksCount = 3
    private var tasksReady = 0

    private var answers = BooleanArray(4)

    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm).apply { start() }
        setupViews()
        resumeQuiz()
    }


    private fun resumeQuiz() = with(binding) {
        if (tasksCount != tasksReady) {

            tvCount.text = "$tasksReady/$tasksCount"

            val firstNum = (0..maxNumber).random()
            val secondNum = (0..maxNumber).random()
            val sum = firstNum + secondNum

            answers.indices.forEach { answers[it] = false }
            answers[(0..3).random()] = true

            val deltaRange = (-maxNumber / 2)..(maxNumber / 2)

            tv0.text = if (answers[0]) sum.toString() else (sum + (deltaRange).random()).toString()
            tv1.text = if (answers[1]) sum.toString() else (sum + (deltaRange).random()).toString()
            tv2.text = if (answers[2]) sum.toString() else (sum + (deltaRange).random()).toString()
            tv3.text = if (answers[3]) sum.toString() else (sum + (deltaRange).random()).toString()

            tvTask.text = "$firstNum + $secondNum"
        } else {
            finish()
        }
    }

    private fun checkAnswer(isRight: Boolean) {
        tasksReady++
        if (!isRight) {
            tasksCount += 2
            Snackbar.make(binding.root, "Неверно, +2 задачи!", Snackbar.LENGTH_SHORT).apply {
                setBackgroundTint(getColor(R.color.design_default_color_error))
                setTextColor(getColor(android.R.color.white))
                show()
            }
        }
        resumeQuiz()
    }

    private fun setupViews() = with(binding) {

        btnStop.setOnClickListener { finish() }

        tvCount.setCharacterLists(TickerUtils.provideNumberList())
        tv0.setCharacterLists(TickerUtils.provideNumberList())
        tv1.setCharacterLists(TickerUtils.provideNumberList())
        tv2.setCharacterLists(TickerUtils.provideNumberList())
        tv3.setCharacterLists(TickerUtils.provideNumberList())

        btn0.setOnClickListener {
            checkAnswer(answers[0])
        }

        btn1.setOnClickListener {
            checkAnswer(answers[1])
        }

        btn2.setOnClickListener {
            checkAnswer(answers[2])
        }

        btn3.setOnClickListener {
            checkAnswer(answers[3])
        }

        tvTime.text = Date().format("HH:mm")
    }

    override fun onBackPressed() {
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }
}

