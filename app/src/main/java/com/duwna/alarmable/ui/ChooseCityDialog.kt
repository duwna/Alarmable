package com.duwna.alarmable.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.duwna.alarmable.R
import com.duwna.alarmable.databinding.DialogChooseCityBinding
import com.duwna.alarmable.utils.collectOnLifecycle
import com.duwna.alarmable.viewmodels.ChooseCityViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChooseCityDialog : DialogFragment(R.layout.dialog_choose_city) {

    private val binding: DialogChooseCityBinding by viewBinding()
    private val viewModel: ChooseCityViewModel by viewModel()

    private val locationPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> {
                    viewModel.getCurrentLocation()
                }
                !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                    showOpenSettingsDialog()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        subscribeOnState()
        getCurrentLocation()
    }

    private fun setupViews() = with(binding) {
        viewModel.subscribeOnSearchQuery(cityNameChangesFlow())

        ivRefresh.setOnClickListener { getCurrentLocation() }
        btnChangeMode.setOnClickListener { viewModel.toggleSearchMode() }

        btnSave.setOnClickListener {
            viewModel.saveLocation()
            dismiss()
        }
    }


    private fun subscribeOnState() = with(binding) {

        viewModel.currentLocation.collectOnLifecycle(viewLifecycleOwner) { locationState ->
            tvMyLocation.text = when (locationState) {
                is ChooseCityViewModel.LocationState.Empty -> "..."
                is ChooseCityViewModel.LocationState.Success -> locationState.city.name
                is ChooseCityViewModel.LocationState.Error -> locationState.message
            }
        }

        viewModel.searchedLocation.collectOnLifecycle(viewLifecycleOwner) { locationState ->
            when (locationState) {
                is ChooseCityViewModel.LocationState.Empty -> {
                    tilSearch.error = null
                    tilSearch.helperText = null
                }
                is ChooseCityViewModel.LocationState.Error -> {
                    tilSearch.error = locationState.message
                }
                is ChooseCityViewModel.LocationState.Success -> {
                    tilSearch.error = null
                    tilSearch.helperText = "Найдено: ${locationState.city.name}"
                }
            }
        }

        viewModel.isSearchMode.collectOnLifecycle(viewLifecycleOwner) { isSearch ->
            groupCurrentLocation.isVisible = !isSearch
            tilSearch.isVisible = isSearch
            btnChangeMode.text = if (isSearch) "Местоположение" else "Поиск"
        }
    }

    private fun cityNameChangesFlow(): Flow<String> = callbackFlow {
        val textWatcher = binding.etSearchCity.doOnTextChanged { text, _, _, _ ->
            trySend(text.toString())
        }
        awaitClose { binding.etSearchCity.removeTextChangedListener(textWatcher) }
    }

    private fun showOpenSettingsDialog() {
        AlertDialog.Builder(context)
            .setTitle("Необходимо разрешение")
            .setMessage("Для получения последнего местоположения необходимо дать разрешение в настройках")
            .setPositiveButton("Открыть настройки") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:com.duwna.alarmable")
                }
                startActivity(intent)
            }
            .show()
    }

    private fun getCurrentLocation() {
        locationPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

}