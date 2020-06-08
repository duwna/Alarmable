package com.duwna.alarmable.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

abstract class BaseViewModel: ViewModel() {

    private val notifications = MutableLiveData<Event<Notify>>()

    protected fun notify(content: Notify) {
        notifications.postValue(Event(content))
    }

    protected fun doAsync(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (t: Throwable) {
                t.printStackTrace()
                notify(Notify.Error())
            }
        }
    }


    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner,
            EventObserver { onNotify(it) })
    }

}

class Event<out E>(private val content: E) {
    var hasBeenHandled = false
    fun getContentIfNotHandled(): E? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }
}

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit) : Observer<Event<E>> {
    override fun onChanged(event: Event<E>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

sealed class Notify {
    abstract val message: String

    data class TextMessage(override val message: String) : Notify()

    data class InternetError(
        override val message: String = "Отсутствует подключение к интернету"
    ) : Notify()

    data class Error(
        override val message: String = "Возникла ошибка загрузки данных"
    ) : Notify()

    data class ActionMessage(
        override val message: String,
        val actionLabel: String,
        val actionHandler: (() -> Unit)
    ) : Notify()
}