package com.duwna.alarmable.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.duwna.alarmable.R
import com.duwna.alarmable.viewmodels.MainViewModel
import com.duwna.alarmable.viewmodels.Notify
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var alarmAdapter: AlarmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setupViews()
    }

    private fun setupViews() {

        alarmAdapter = AlarmAdapter(viewModel.setListeners())

        rv_bills.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alarmAdapter
        }

        viewModel.alarms.observe(this, Observer {
            tv_no_alarms.isVisible = it.isEmpty()
            alarmAdapter.submitList(it)
        })

        viewModel.observeNotifications(this) { renderNotification(it) }

        fab.setOnClickListener {
            showTimePickerDialog()
//            viewModel.addAlarm()
        }
    }

    private fun renderNotification(notify: Notify) {
        val snackBar = Snackbar.make(container, notify.message, Snackbar.LENGTH_LONG)
        when (notify) {
            is Notify.ActionMessage ->
                snackBar.setAction(notify.actionLabel) { notify.actionHandler.invoke() }
        }
        snackBar.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val listener = { _: TimePicker, hourOfDay: Int, minute: Int ->
            viewModel.addAlarm(hourOfDay, minute)
        }
        TimePickerDialog(
            this,
            listener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
}