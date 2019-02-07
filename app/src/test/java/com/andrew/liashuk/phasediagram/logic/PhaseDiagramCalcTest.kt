package com.andrew.liashuk.phasediagram.logic

import org.junit.Test

import org.junit.Assert.*

class PhaseDiagramCalcTest {

    @Test
    fun calculatedDiagramNotEmpty() {
        val phaseDiagram = PhaseDiagramCalc(
            1000.0,
            2000.0,
            20.0,
            30.0,
            10000.0,
            -10000.0,
            0.0,
            20000.0
        )
        val result = phaseDiagram.calculatePhaseDiagram()
        assertTrue(result.isNotEmpty())
    }
}