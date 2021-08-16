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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.duwna.alarmable.R
import com.duwna.alarmable.databinding.ActivityMainBinding
import com.duwna.alarmable.ui.adapters.AlarmAdapter
import com.duwna.alarmable.utils.PERMISSION_REQUEST_CODE
import com.duwna.alarmable.utils.PICK_MELODY_CODE
import com.duwna.alarmable.viewmodels.MainViewModel
import com.duwna.alarmable.viewmodels.Notify
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding: ActivityMainBinding by viewBinding()
    private val viewModel: MainViewModel by viewModel()

    private val alarmAdapter: AlarmAdapter by lazy {
        AlarmAdapter(
            listener = viewModel.setListeners(),
            onMelodyClicked = { alarm ->
                viewModel.tempAlarm.value = alarm
                chooseMelody()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        setupViews()
        subscribeOnState()
    }

    private fun subscribeOnState() {

        lifecycleScope.launch {
            viewModel.alarms.collect { alarms ->
                binding.tvNoAlarms.isVisible = alarms.isEmpty()
                alarmAdapter.submitList(alarms)
            }
        }
    }

    private fun setupViews() {

        binding.rvAlarms.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alarmAdapter
        }

        viewModel.observeNotifications(this) { renderNotification(it) }

        binding.fab.setOnClickListener {
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
        Snackbar.make(binding.root, notify.message, Snackbar.LENGTH_LONG).show()
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    chooseMelody()
                } else {
                    Snackbar.make(
                        binding.root,
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