package com.duwna.alarmable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.duwna.alarmable.ui.AlarmAdapter
import com.duwna.alarmable.utils.MockGenerator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val alarmAdapter = AlarmAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        rv_bills.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alarmAdapter
        }

        alarmAdapter.submitList(MockGenerator.generateAlarmList(5))
    }
}