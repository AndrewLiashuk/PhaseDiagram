package com.andrew.liashuk.phasediagram.ext

import android.view.View
import android.view.ViewTreeObserver

/**
 * Waits until the view has been through at least one layout since it was last attached to or detached from a window.
 */
inline fun View.onLaidOut(crossinline action: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            // Removing layout listener to avoid multiple calls
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            action()
        }
    })
}