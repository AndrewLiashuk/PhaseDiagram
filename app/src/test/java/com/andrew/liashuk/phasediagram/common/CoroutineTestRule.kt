package com.andrew.liashuk.phasediagram.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@ExperimentalCoroutinesApi
class CoroutineTestRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestRule {

    override fun apply(base: Statement, description: Description): Statement =
        object : Statement() {

            @Throws(Throwable::class)
            override fun evaluate() {
                // Rethrows uncaught exceptions into the test's thread.
                // Otherwise, the test will pass successfully with an exception in another thread.
                // https://github.com/Kotlin/kotlinx.coroutines/issues/1205
                val oldDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
                Thread.setDefaultUncaughtExceptionHandler { _, throwable -> throw throwable }

                Dispatchers.setMain(testDispatcher)
                base.evaluate()
                Dispatchers.resetMain()

                Thread.setDefaultUncaughtExceptionHandler(oldDefaultUncaughtExceptionHandler)
            }
        }
}
