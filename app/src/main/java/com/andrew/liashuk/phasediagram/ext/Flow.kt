package com.andrew.liashuk.phasediagram.ext

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Terminal flow operator that launches a new coroutine without blocking the current thread and
 * collects the given flow with a provided [action].
 * [action] runs when [owner] is at least at [minActiveState] and suspends the execution until
 * [owner] is [Lifecycle.State.DESTROYED].
 *
 * @param owner The LifecycleOwner which controls the coroutine.
 * @param minActiveState [Lifecycle.State] in which `block` runs in a new coroutine. That coroutine
 * will cancel if the lifecycle falls below that state, and will restart if it's in that state
 * again.
 * @param action The block to run when the lifecycle is at least in [minActiveState] state.
 */
inline fun <T> Flow<T>.collectWithLifecycle(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend (value: T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        this@collectWithLifecycle.collect {
            action(it)
        }
    }
}

/**
 * Extension function that allows an easier call to the API from [Fragment].
 *
 * @see Flow.collectWithLifecycle
 */
inline fun <T> Flow<T>.collectWithLifecycle(
    fragment: Fragment,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend (value: T) -> Unit
) {
    collectWithLifecycle(fragment.viewLifecycleOwner, minActiveState, action)
}

inline fun <T> Flow<T>.collect(
    scope: CoroutineScope,
    crossinline action: suspend (value: T) -> Unit
) {
    scope.launch {
        this@collect.collect {
            action(it)
        }
    }
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

fun <T> StateFlow<T?>.isEmpty(): Boolean = value == null

fun <T> StateFlow<T?>.isNotEmpty(): Boolean = value != null

suspend fun <T> StateFlow<T?>.firstNotNull(): T = this.first { it != null }!!