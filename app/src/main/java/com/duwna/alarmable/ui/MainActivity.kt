package com.duwna.alarmable.ui

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Menu
import android.view.MenuItem
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.duwna.alarmable.R
import com.duwna.alarmable.ui.adapters.AlarmAdapter
import com.duwna.alarmable.utils.PERMISSION_REQUEST_CODE
import com.duwna.alarmable.utils.PICK_MELODY_CODE
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

        alarmAdapter = AlarmAdapter(
            listener = viewModel.setListeners(),
            onMelodyClicked = { alarm ->
                viewModel.tempAlarm.value = alarm
                chooseMelody()
            }
        )

        rv_alarms.apply {
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
        }

    }

    private fun chooseMelody() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, null as Uri?)
        }
        if (!isPermitted()) request() else startActivityForResult(intent, PICK_MELODY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_MELODY_CODE) {
            val uri = data?.extras?.get(RingtoneManager.EXTRA_RINGTONE_PICKED_URI) as Uri?
            setMelody(uri)
        }
    }

    private fun setMelody(uri: Uri?) {
        uri?.let { returnUri ->
            contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            val name = cursor.getString(nameIndex)
            viewModel.setMelody(name)
        }
    }

    private fun renderNotification(notify: Notify) {
        Snackbar.make(container, notify.message, Snackbar.LENGTH_LONG).show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val listener = { _: TimePicker, hourOfDay: Int, minute: Int ->
            viewModel.addAlarm(hourOfDay, minute)
            setAlarm(hourOfDay, minute)
        }
        TimePickerDialog(
            this,
            listener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun setAlarm(hourOfDay: Int, minute: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        val intent = Intent(this@MainActivity, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this@MainActivity,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            pendingIntent
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.task -> {
                startActivity(Intent(this, TaskActivity::class.java))
                true
            }
            R.id.info -> {
                startActivity(Intent(this, InfoActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isPermitted(): Boolean = ActivityCompat
        .checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun request() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    chooseMelody()
                } else {
                    Snackbar.make(
                        container,
                        "Для выбора мелодии необходимо дать разрешение на чтение хранилища",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                return
            }
            else -> {

            }
        }
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startActivity(
            Intent(
                context.applicationContext,
                TaskActivity::class.java
            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
    }
}