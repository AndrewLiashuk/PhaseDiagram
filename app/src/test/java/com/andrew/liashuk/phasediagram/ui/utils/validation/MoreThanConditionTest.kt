package com.andrew.liashuk.phasediagram.ui.utils.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MoreThanConditionTest {

    @Test
    fun `should return false on lower number`() {
        val condition = MoreThanCondition(value = 10.0, inclusive = true)
        listOf("-10", "-5.123", "-1.0", "0", "1.0", "5", "9.999999").forEach { testCase ->
            assertFalse("Problem with value: \"$testCase\"", condition.check(input = testCase))
        }
    }

    @Test
    fun `should return false on default inclusivity and same value`() {
        val condition = MoreThanCondition(value = 10.0)
        assertFalse(condition.check(input = "10"))
    }

    @Test
    fun `should return true on default inclusivity and bit bigger number`() {
        val condition = MoreThanCondition(value = 10.0)
        assertTrue(condition.check(input = "10.01"))
    }

    @Test
    fun `should return true on bigger number`() {
        val condition = MoreThanCondition(value = 10.0, inclusive = true)
        listOf("10", "10.0", "10.1", "100.001", "1000000000").forEach { testCase ->
            assertTrue("Problem with value: \"$testCase\"", condition.check(input = testCase))
        }
    }

    @Test
    fun `should return true on empty input`() {
        val condition = MoreThanCondition(value = 10.0)
        assertTrue(condition.check(input = ""))
        assertTrue(condition.check(input = null))
    }

    @Test
    fun `should return false if can't parse number`() {
        val condition = MoreThanCondition(value = 10.0, inclusive = true)
        listOf("-", ".", "100t", "text101text", "cvsd,fv,.df v", "≈∑≤¬µ˜q∑ç").forEach { testCase ->
            assertFalse("Problem with value: \"$testCase\"", condition.check(input = testCase))
        }
    }
}