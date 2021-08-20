package com.duwna.alarmable.ui

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.duwna.alarmable.R
import com.duwna.alarmable.utils.collectOnLifecycle
import com.duwna.alarmable.viewmodels.BaseViewModel
import com.duwna.alarmable.viewmodels.SnackBarEvent
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity<VM : BaseViewModel, B : ViewBinding>(
    @LayoutRes layoutRes: Int
) : AppCompatActivity(layoutRes) {

    abstract val binding: B
    abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.snackbarFlow.collectOnLifecycle(this) { event ->
            when (event) {
                is SnackBarEvent.Message -> showMessageSnackbar(event)
                is SnackBarEvent.ResMessage -> showResMessageSnackbar(event)
                is SnackBarEvent.Error -> showErrorSnackbar(event)
                is SnackBarEvent.Action -> showActionSnackbar(event)
            }
        }
    }

    private fun showMessageSnackbar(event: SnackBarEvent.Message) {
        Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun showResMessageSnackbar(event: SnackBarEvent.ResMessage) {
        Snackbar.make(binding.root, event.resId, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun showErrorSnackbar(event: SnackBarEvent.Error) {
        Snackbar.make(
            binding.root,
            event.message,
            Snackbar.LENGTH_SHORT
        ).apply {
            setBackgroundTint(getColor(R.color.design_default_color_error))
            setTextColor(getColor(android.R.color.white))
            show()
        }
    }

    private fun showActionSnackbar(event: SnackBarEvent.Action) {
        Snackbar.make(
            binding.root,
            event.message,
            Snackbar.LENGTH_SHORT
        ).apply {
            setAction(event.actionLabel) { event.onClick() }
            show()
        }
    }
}

