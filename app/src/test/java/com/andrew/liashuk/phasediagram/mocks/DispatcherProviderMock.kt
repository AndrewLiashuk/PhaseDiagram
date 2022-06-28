package com.andrew.liashuk.phasediagram.mocks

import com.andrew.liashuk.phasediagram.common.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

class DispatcherProviderMock(private val dispatcher: CoroutineDispatcher) : DispatcherProvider {
    override fun io() = dispatcher
    override fun main() = dispatcher
    override fun default() = dispatcher
}