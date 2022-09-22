package com.andrew.liashuk.phasediagram.types

import com.andrew.liashuk.phasediagram.model.SolutionType
import org.junit.Assert.*
import org.junit.Test

class PhaseDataTest {

    @Test
    fun convertStringVaribaleToDouble() {
        val phaseData = PhaseData()

        phaseData.meltingTempFirstStr = "10"
        assertEquals(
            "Can't convert first melting temp from str variable",
            phaseData.meltingTempFirst,
            10.0
        )

        phaseData.meltingTempSecondStr = "20.0"
        assertEquals(
            "Can't convert second melting temp from str variable",
            phaseData.meltingTempSecond,
            20.0
        )

        phaseData.entropFirstStr = "5.220"
        assertEquals(
            "Can't convert first entropy from str variable",
            phaseData.entropyFirst,
            5.22
        )

        phaseData.entropSecondStr = "0.5"
        assertEquals(
            "Can't convert second entropy from str variable",
            phaseData.entropySecond,
            0.5
        )

        phaseData.alphaLFirstStr = "10000"
        assertEquals(
            "Can't convert first alphaL from str variable",
            phaseData.alphaLFirst,
            10000.0
        )

        phaseData.alphaLSecondStr= "20000.000"
        assertEquals(
            "Can't convert second alphaL from str variable",
            phaseData.alphaLSecond,
            20000.0
        )

        phaseData.alphaSFirstStr = "-10000"
        assertEquals(
            "Can't convert first alphaS from str variable",
            phaseData.alphaSFirst,
            -10000.0
        )

        phaseData.alphaSSecondStr = "-12000.25"
        assertEquals(
            "Can't convert second alphaS from str variable",
            phaseData.alphaSSecond,
            -12000.25
        )
    }


    @Test
    fun getStringValueOfDoubleVariable() {
        val phaseData = PhaseData()

        phaseData.meltingTempFirst = 10.0
        assertEquals(
            "Can't convert first melting temp from double variable to string",
            phaseData.meltingTempFirstStr,
            "10"
        )

        phaseData.meltingTempSecond = 20.1
        assertEquals(
            "Can't convert second melting temp from double variable to string",
            phaseData.meltingTempSecondStr,
            "20.1"
        )

        phaseData.entropyFirst = 5.220
        assertEquals(
            "Can't convert first entropy from double variable to string",
            phaseData.entropFirstStr,
            "5.22"
        )

        phaseData.entropySecond = 0.5
        assertEquals(
            "Can't convert second entropy from double variable to string",
            phaseData.entropSecondStr,
            "0.5"
        )

        phaseData.alphaLFirst = 10000.000
        assertEquals(
            "Can't convert first alphaL from double variable to string",
            phaseData.alphaLFirstStr,
            "10000"
        )

        phaseData.alphaLSecond = 20000.010
        assertEquals(
            "Can't convert second alphaL from double variable to string",
            phaseData.alphaLSecondStr,
            "20000.01"
        )

        phaseData.alphaSFirst = -10000.0
        assertEquals(
            "Can't convert first alphaS from double variable to string",
            phaseData.alphaSFirstStr,
            "-10000"
        )

        phaseData.alphaSSecond = -12000.25
        assertEquals(
            "Can't convert second alphaS from double variable to string",
            phaseData.alphaSSecondStr,
            "-12000.25"
        )
    }


    @Test
    fun changeSolutionTypeToSubregular() {
        val phaseDiagram = PhaseData(1000.0,2000.0,20.0,
            30.0,10000.0,-10000.0,0.0,20000.0)

        val phaseDiagramStandard = PhaseData(1000.0,2000.0,20.0,
            30.0,10000.0,-10000.0,0.0,20000.0)

        phaseDiagram.changeType(SolutionType.SUBREGULAR)
        assertEquals(phaseDiagram, phaseDiagramStandard)
    }


    @Test
    fun changeSolutionTypeToRegular() {
        val phaseDiagram = PhaseData(1000.0,2000.0,20.0,
            30.0,10000.0,-10000.0,0.0,20000.0)

        val phaseDiagramStandard = PhaseData(1000.0,2000.0,20.0,
            30.0,10000.0,-10000.0,null,null)

        phaseDiagram.changeType(SolutionType.REGULAR)
        assertEquals(phaseDiagram, phaseDiagramStandard)
    }


    @Test
    fun changeSolutionTypeToIdeal() {
        val phaseDiagram = PhaseData(1000.0,2000.0,20.0,
            30.0,10000.0,-10000.0,0.0,20000.0)

        val phaseDiagramStandard = PhaseData(1000.0,2000.0,20.0,
            30.0,null,null,null,null)

        phaseDiagram.changeType(SolutionType.IDEAL)
        assertEquals(phaseDiagram, phaseDiagramStandard)
    }


    @Test
    fun testCheckDataOnFullData() {
        val phaseDiagram = PhaseData(1000.0,2000.0,20.0,
            30.0,10000.0,-10000.0,0.0,20000.0)

        val result = phaseDiagram.checkData(SolutionType.SUBREGULAR)
        assert(result == null)
    }


    @Test
    fun testCheckDataOnMissSomeData() {
        val phaseDiagram = PhaseData(1000.0,2000.0,20.0,
            30.0,10000.0,-10000.0,0.0,20000.0)

        phaseDiagram.meltingTempFirst = null
        val meltingTempFirstRes = phaseDiagram.checkData(SolutionType.SUBREGULAR)
        assertTrue("Not detect missing meltingTempFirst",meltingTempFirstRes != null)
        phaseDiagram.meltingTempFirst = 0.0

        phaseDiagram.meltingTempSecond = null
        val meltingTempSecond = phaseDiagram.checkData(SolutionType.SUBREGULAR)
        assertTrue("Not detect missing meltingTempSecond",meltingTempSecond != null)
        phaseDiagram.meltingTempSecond = 0.0

        phaseDiagram.entropyFirst = null
        val entropFirst = phaseDiagram.checkData(SolutionType.SUBREGULAR)
        assertTrue("Not detect missing entropFirst",entropFirst != null)
        phaseDiagram.entropyFirst = 0.0

        phaseDiagram.entropySecond = null
        val entropSecond = phaseDiagram.checkData(SolutionType.SUBREGULAR)
        assertTrue("Not detect missing entropSecond",entropSecond != null)
        phaseDiagram.entropySecond = 0.0

        phaseDiagram.alphaSFirst = null
        val alphaSFirst = phaseDiagram.checkData(SolutionType.SUBREGULAR)
        assertTrue("Not detect missing alphaSFirst",alphaSFirst != null)
        phaseDiagram.alphaSFirst = 0.0

        phaseDiagram.alphaSSecond = null
        val alphaSSecond = phaseDiagram.checkData(SolutionType.SUBREGULAR)
        assertTrue("Not detect missing alphaSSecond",alphaSSecond != null)
        phaseDiagram.alphaSSecond = 0.0

        phaseDiagram.alphaLFirst = null
        val alphaLFirst = phaseDiagram.checkData(SolutionType.SUBREGULAR)
        assertTrue("Not detect missing alphaLFirst",alphaLFirst != null)
        phaseDiagram.alphaLFirst = 0.0

        phaseDiagram.alphaLSecond = null
        val alphaLSecond = phaseDiagram.checkData(SolutionType.SUBREGULAR)
        assertTrue("Not detect missing alphaLSecond",alphaLSecond != null)
        phaseDiagram.alphaLSecond = 0.0
    }
}