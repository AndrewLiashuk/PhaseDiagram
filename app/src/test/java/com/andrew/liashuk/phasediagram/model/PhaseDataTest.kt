package com.andrew.liashuk.phasediagram.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PhaseDataTest {

    private val newValue = 10.0

    @Test
    fun `should change only meltingTempFirst in copy`() {
        val expected = createData(meltingTempFirst = newValue)
        val copy = createData().copy(Elements.MELTING_TEMPERATURE_FIRST, newValue)

        assertEquals(expected, copy)
    }

    @Test
    fun `should change only meltingTempSecond in copy`() {
        val expected = createData(meltingTempSecond = newValue)
        val copy = createData().copy(Elements.MELTING_TEMPERATURE_SECOND, newValue)

        assertEquals(expected, copy)
    }

    @Test
    fun `should change only entropyFirst in copy`() {
        val expected = createData(entropyFirst = newValue)
        val copy = createData().copy(Elements.ENTROPY_FIRST, newValue)

        assertEquals(expected, copy)
    }

    @Test
    fun `should change only entropySecond in copy`() {
        val expected = createData(entropySecond = newValue)
        val copy = createData().copy(Elements.ENTROPY_SECOND, newValue)

        assertEquals(expected, copy)
    }

    @Test
    fun `should change only alphaLFirst in copy`() {
        val expected = createData(alphaLFirst = newValue)
        val copy = createData().copy(Elements.ALPHA_L_FIRST, newValue)

        assertEquals(expected, copy)
    }

    @Test
    fun `should change only alphaSFirst in copy`() {
        val expected = createData(alphaSFirst = newValue)
        val copy = createData().copy(Elements.ALPHA_S_FIRST, newValue)

        assertEquals(expected, copy)
    }

    @Test
    fun `should change only alphaLSecond in copy`() {
        val expected = createData(alphaLSecond = newValue)
        val copy = createData().copy(Elements.ALPHA_L_SECOND, newValue)

        assertEquals(expected, copy)
    }

    @Test
    fun `should change only alphaSSecond in copy`() {
        val expected = createData(alphaSSecond = newValue)
        val copy = createData().copy(Elements.ALPHA_S_SECOND, newValue)

        assertEquals(expected, copy)
    }

    private fun createData(
        meltingTempFirst: Double = 1.0,
        meltingTempSecond: Double = 2.0,
        entropyFirst: Double = 3.0,
        entropySecond: Double = 4.0,
        alphaLFirst: Double = 5.0,
        alphaSFirst: Double = 6.0,
        alphaLSecond: Double = 7.0,
        alphaSSecond: Double = 8.0,
    ) = PhaseData(
        meltingTempFirst = meltingTempFirst,
        meltingTempSecond = meltingTempSecond,
        entropyFirst = entropyFirst,
        entropySecond = entropySecond,
        alphaLFirst = alphaLFirst,
        alphaSFirst = alphaSFirst,
        alphaLSecond = alphaLSecond,
        alphaSSecond = alphaSSecond,
    )
}