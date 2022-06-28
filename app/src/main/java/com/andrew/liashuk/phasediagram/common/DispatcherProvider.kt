package com.andrew.liashuk.phasediagram.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {

    /**
     * Dispatcher for offloading blocking IO tasks
     */
    fun io(): CoroutineDispatcher

    /**
     * Dispatcher for updating UI
     */
    fun main(): CoroutineDispatcher

    /**
     * Dispatcher that bounded to cpu core number
     */
    fun default(): CoroutineDispatcher
}

class DefaultDispatcherProviderImpl : DispatcherProvider {
    override fun io() = Dispatchers.IO
    override fun main() = Dispatchers.Main
    override fun default() = Dispatchers.Default
}