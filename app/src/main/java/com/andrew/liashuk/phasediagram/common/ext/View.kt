package com.andrew.liashuk.phasediagram.common.ext

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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

fun EditText.textChanges(): Flow<String?> = callbackFlow {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(editable: Editable) = Unit
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            trySend(text?.toString())
        }
    }

    addTextChangedListener(watcher)

    awaitClose { removeTextChangedListener(watcher) }
}

fun View.getWindowInsets(listener: View.(Insets) -> Unit) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
        val insets = windowInsets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        listener(insets)
        WindowInsetsCompat.CONSUMED
    }
}

fun View.applyWindowInsetsToPaddings() {
    getWindowInsets { insets ->
        // Apply padding to the view to move its content away from the system bars
        updatePadding(
            left = insets.left,
            top = insets.top,
            right = insets.right,
            bottom = insets.bottom
        )
    }
}

fun <T : ViewBinding> T.applyWindowInsetsToPaddings() = apply {
    root.applyWindowInsetsToPaddings()
}