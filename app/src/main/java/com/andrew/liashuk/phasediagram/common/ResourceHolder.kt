package com.andrew.liashuk.phasediagram.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> resourceHolder(
    ownerProducer: () -> LifecycleOwner,
    initValue: T? = null,
    cleanupState: Lifecycle.State = Lifecycle.State.DESTROYED
) = object : ReadWriteProperty<Any?, T> {

    private var resource: T? = initValue

    private val observer = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (source.lifecycle.currentState == cleanupState) {
                resource = null
                ownerProducer().lifecycle.removeObserver(this)
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return resource ?: throw IllegalStateException("The resource is not defined or was cleaned up")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        ownerProducer().lifecycle.addObserver(observer)
        resource = value
    }
}

fun <T> Fragment.resourceHolder(
    initValue: T? = null,
    cleanupState: Lifecycle.State = Lifecycle.State.DESTROYED
): ReadWriteProperty<Any?, T> = resourceHolder(
    ownerProducer = { this.viewLifecycleOwner },
    initValue = initValue,
    cleanupState = cleanupState
)