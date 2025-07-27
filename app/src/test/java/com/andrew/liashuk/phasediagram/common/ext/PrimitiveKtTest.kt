package com.andrew.liashuk.phasediagram.common.ext

import org.junit.Assert.assertEquals
import org.junit.Test

class PrimitiveTest {

    @Test
    fun `should return empty string on null`() {
        val value: Double? = null
        assertEquals("", value.toPrettyString())
    }

    @Test
    fun `should return number without fractional part`() {
        val value = 20.00
        assertEquals("20", value.toPrettyString())
    }

    @Test
    fun `should return number without last 0 in fractional part`() {
        val value = 20.10
        assertEquals("20.1", value.toPrettyString())
    }

    @Test
    fun `should return number same as value`() {
        val value = 20.11
        assertEquals("20.11", value.toPrettyString())
    }

    @Test
    fun `should return number even with 0 but only in the middle of fractional part`() {
        val value = 20.101
        assertEquals("20.101", value.toPrettyString())
    }
}