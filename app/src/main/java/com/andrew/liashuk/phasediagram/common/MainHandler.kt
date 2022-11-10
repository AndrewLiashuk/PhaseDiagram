package com.andrew.liashuk.phasediagram.common

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@RequiresOptIn(message = "This API is experimental", level = RequiresOptIn.Level.WARNING)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalHandler

@ExperimentalHandler
class MainHandler {

    @Volatile
    private var mainHandler: Handler? = null

    val isActive: Boolean
        get() = mainHandler != null

    fun postAction(delayMillis: Long = 0, action: () -> Unit) {
        mainHandler?.postDelayed(action, delayMillis)
    }

    fun start() {
        if (mainHandler == null) {
            synchronized(this) {
                if (mainHandler == null) {
                    mainHandler = Handler(Looper.getMainLooper())
                }
            }
        }
    }

    fun finish() {
        mainHandler?.removeCallbacksAndMessages(null)
        mainHandler = null // set null to prevent adding action to Handler after destroy
    }
}

@ExperimentalHandler
fun mainHandler(ownerProducer: () -> LifecycleOwner) = object : ReadOnlyProperty<Any?, MainHandler> {

    private var mainHandler: MainHandler? = null

    private val observer = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (source.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                mainHandler?.finish()
                ownerProducer().lifecycle.removeObserver(this)
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): MainHandler {
        if (mainHandler == null) {
            mainHandler = MainHandler()
        }

        if (mainHandler?.isActive == false &&
            ownerProducer().lifecycle.currentState != Lifecycle.State.DESTROYED
        ) {
            mainHandler?.start()
            ownerProducer().lifecycle.addObserver(observer)
        }

        return mainHandler!!
    }
}

fun Fragment.mainHandler() = mainHandler(ownerProducer = { this.viewLifecycleOwner })