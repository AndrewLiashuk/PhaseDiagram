package com.andrew.liashuk.phasediagram.common

import kotlinx.coroutines.channels.Channel

abstract class Event

class ShowProgress : Event()

class HideProgress : Event()

class ShowToast(val message: String) : Event()

fun Channel<Event>.showProgress(show: Boolean) {
    trySend(if (show) ShowProgress() else HideProgress())
}

inline fun <T> Channel<Event>.withProgress(block: () -> T) : T {
    showProgress(true)
    return block().also {
        showProgress(false)
    }
}

fun Channel<Event>.showToast(message: String) {
    trySend(ShowToast(message))
}