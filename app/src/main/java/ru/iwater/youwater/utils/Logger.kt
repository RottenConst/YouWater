@file:Suppress("DEPRECATION")

package ru.iwater.youwater.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber

class Logger: LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun logEvent(
        owner: LifecycleOwner,
        event: Lifecycle.Event
    ) {
        val message = owner.javaClass.simpleName
        val event1 = event.targetState.name
        Timber.d("message $message event $event1")
    }
}