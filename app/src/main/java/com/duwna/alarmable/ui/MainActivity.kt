package com.duwna.alarmable.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.duwna.alarmable.R
import com.duwna.alarmable.databinding.ActivityMainBinding
import com.duwna.alarmable.ui.adapters.AlarmAdapter
import com.duwna.alarmable.utils.collectOnLifecycle
import com.duwna.alarmable.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(R.layout.activity_main) {

    override val binding: ActivityMainBinding by viewBinding()
    override val viewModel: MainViewModel by viewModel()

    private val alarmAdapter: AlarmAdapter by lazy {
        AlarmAdapter(
            listener = viewModel.setListeners(),
            onMelodyClicked = { alarm ->
                viewModel.pendingAlarm.value = alarm
                chooseMelody()
            }
        )
    }

    private val storagePermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> {
                    openMelodyChooser()
                }
                !shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showOpenSettingsDialog()
                }
            }
        }

    private val melodyChooseResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri =
                    result.data?.extras?.get(RingtoneManager.EXTRA_RINGTONE_PICKED_URI) as Uri?
                if (uri != null) setMelody(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        setupViews()
        subscribeOnState()
    }

    private fun openMelodyChooser() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, null as Uri?)
        }
        melodyChooseResult.launch(intent)
    }

    private fun subscribeOnState() {
        viewModel.alarms.collectOnLifecycle(this) { alarms ->
            binding.tvNoAlarms.isVisible = alarms.isEmpty()
            alarmAdapter.submitList(alarms)
        }
    }

    private fun setupViews() {
        binding.rvAlarms.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alarmAdapter
        }
        binding.fab.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun chooseMelody() {
        storagePermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun setMelody(uri: Uri) {
        uri.let { returnUri ->
            contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()

            val name = cursor.getString(nameIndex)
                .substringBefore(".")
                .replace("_", " ")

            viewModel.setMelody(name, uri.toString())
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hourOfDay: Int, minute: Int -> viewModel.onTimePicked(hourOfDay, minute) },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
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
            R.id.settings -> {
                ChooseCityDialog().show(supportFragmentManager, "ChooseCityDialog")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showOpenSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Необходимо разрешение")
            .setMessage("Для выбора мелодии необходимо дать разрешение на чтение хранилища")
            .setPositiveButton("Открыть настройки") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:com.duwna.alarmable")
                }
                startActivity(intent)
            }
            .show()
    }
}