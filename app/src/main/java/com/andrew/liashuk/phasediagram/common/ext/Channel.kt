package com.andrew.liashuk.phasediagram.common.ext

import com.andrew.liashuk.phasediagram.common.Event
import com.andrew.liashuk.phasediagram.common.HideProgress
import com.andrew.liashuk.phasediagram.common.ShowProgress
import com.andrew.liashuk.phasediagram.common.ShowToast
import kotlinx.coroutines.channels.Channel

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