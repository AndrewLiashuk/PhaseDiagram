package com.andrew.liashuk.phasediagram.common.ext

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Returns a [MutableStateFlow] that accesses data associated with the given key.
 *
 * @param scope The scope used to synchronize the [MutableStateFlow] and [SavedStateHandle]
 * @param key The identifier for the value
 * @param initialValue If no value exists with the given [key], a new one is created
 * with the given [initialValue].
 */
fun <T> SavedStateHandle.getMutableStateFlow(
    scope: CoroutineScope,
    key: String,
    initialValue: T
): MutableStateFlow<T> = MutableStateFlow(get(key) ?: initialValue).apply {
    scope.launch(Dispatchers.Main.immediate) {
        this@apply.collect { value ->
            set(key, value)
        }
    }
}

/**
 * Returns a [MutableStateFlow] for [ViewModel] that accesses data associated with the given key.
 *
 * @param key The identifier for the value
 * @param initialValue If no value exists with the given [key], a new one is created
 * with the given [initialValue].
 */
context(ViewModel)
fun <T> SavedStateHandle.getMutableStateFlow(
    key: String,
    initialValue: T
): MutableStateFlow<T> = getMutableStateFlow(viewModelScope, key, initialValue)