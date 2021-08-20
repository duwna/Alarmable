package com.duwna.alarmable.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val snackbarChannel = Channel<SnackBarEvent>(Channel.BUFFERED)
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    protected fun showSnackbar(snackBarEvent: SnackBarEvent) {
        viewModelScope.launch { snackbarChannel.send(snackBarEvent) }
    }

    protected fun launchSafety(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (t: Throwable) {
                t.printStackTrace()
                showSnackbar(SnackBarEvent.Error(t.localizedMessage ?: "Возникла ошибка..."))
            }
        }
    }
}

sealed class SnackBarEvent {
    data class Message(val message: String) : SnackBarEvent()
    data class ResMessage(@StringRes val resId: Int): SnackBarEvent()
    data class Error(val message: String) : SnackBarEvent()
    data class Action(
        val message: String,
        val actionLabel: String,
        val onClick: () -> Unit
    ) : SnackBarEvent()
}