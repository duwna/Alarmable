package com.duwna.alarmable.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.SeekBar
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.duwna.alarmable.R
import com.duwna.alarmable.databinding.ActivityTaskBinding
import com.duwna.alarmable.services.AlarmService
import com.duwna.alarmable.utils.collectOnLifecycle
import com.duwna.alarmable.utils.dpToPx
import com.duwna.alarmable.utils.format
import com.duwna.alarmable.viewmodels.TaskViewModel
import com.robinhood.ticker.TickerUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.abs

class TaskActivity : BaseActivity<TaskViewModel, ActivityTaskBinding>(R.layout.activity_task) {

    override val binding: ActivityTaskBinding by viewBinding()
    override val viewModel: TaskViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()
        super.onCreate(savedInstanceState)

        setupViews()
        subscribeOnState()
    }

    private fun subscribeOnState() {

        viewModel.answers.collectOnLifecycle(this) {
            with(binding) {
                tv0.text = it[0].toString()
                tv1.text = it[1].toString()
                tv2.text = it[2].toString()
                tv3.text = it[3].toString()
            }
        }

        viewModel.question.collectOnLifecycle(this) {
            binding.tvTask.text =
                if (it.second < 0) "${it.first} - ${abs(it.second)}" else "${it.first} + ${it.second}"
        }

        viewModel.count.collectOnLifecycle(this) {
            binding.tvCount.text = "${it.first}/${it.second}"
        }

        viewModel.readyEventFlow.collectOnLifecycle(this) {
            finishQuiz()
        }
    }


    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    private fun setupViews() = with(binding) {

        btnDifficulty.setOnClickListener {
            if (seekBar.isVisible) hideSeekBar()
            else showSeekBar()
        }

        tvCount.setCharacterLists(TickerUtils.provideNumberList())
        tv0.setCharacterLists(TickerUtils.provideNumberList())
        tv1.setCharacterLists(TickerUtils.provideNumberList())
        tv2.setCharacterLists(TickerUtils.provideNumberList())
        tv3.setCharacterLists(TickerUtils.provideNumberList())

        btnStop.setOnClickListener { finishQuiz() }
        btn0.setOnClickListener { viewModel.checkAnswer(0) }
        btn1.setOnClickListener { viewModel.checkAnswer(1) }
        btn2.setOnClickListener { viewModel.checkAnswer(2) }
        btn3.setOnClickListener { viewModel.checkAnswer(3) }

        seekBar.progress = viewModel.getDifficulty() / 10

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser && progress > 0) viewModel.applyDifficulty(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewModel.saveDifficulty()
                hideSeekBar()
            }
        })

        tvTime.text = Date().format("HH:mm")
    }

    private fun finishQuiz() {
        stopService(Intent(this, AlarmService::class.java))
        val taskActivityIntent = Intent(this, InfoActivity::class.java).apply {
            putExtras(intent)
        }
        startActivity(taskActivityIntent)
        finish()
    }

    override fun onBackPressed() {}

    private fun showSeekBar() {
        binding.seekBar.animate().apply {
            withStartAction { binding.seekBar.isVisible = true }
            scaleX(1f)
            alpha(1f)
            translationY(-dpToPx(100))
            start()
        }
    }

    private fun hideSeekBar() {
        binding.seekBar.animate().apply {
            withEndAction { binding.seekBar.isVisible = false }
            scaleX(0f)
            alpha(0f)
            translationY(0f)
            start()
        }
    }
}

