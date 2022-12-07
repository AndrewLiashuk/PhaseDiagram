package com.andrew.liashuk.phasediagram.ui.utils.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotEmptyConditionTest {

    private val condition = NotEmptyCondition()

    @Test
    fun `should return false on empty input`() {
        assertFalse(condition.check(input = ""))
    }

    @Test
    fun `should return false on null`() {
        assertFalse(condition.check(input = null))
    }

    @Test
    fun `should return true on random text`() {
        listOf(" ", "something", "Ω≈∑≈∑ßå≈å≈", "100").forEach { testCase ->
            assertTrue("Problem with value: \"$testCase\"", condition.check(input = testCase))
        }
    }
}