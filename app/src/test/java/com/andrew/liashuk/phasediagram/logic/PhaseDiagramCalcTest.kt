package com.andrew.liashuk.phasediagram.logic

import org.junit.Test

import org.junit.Assert.*

class PhaseDiagramCalcTest {

    @Test
    fun calculatePhaseDiagram() {
        val phaseDiagram = PhaseDiagramCalc(
            1000.0,
            2000.0,
            20.0,
            30.0
        )
        val result = phaseDiagram.calculatePhaseDiagram()
        assertTrue(result.isNotEmpty())
    }
}