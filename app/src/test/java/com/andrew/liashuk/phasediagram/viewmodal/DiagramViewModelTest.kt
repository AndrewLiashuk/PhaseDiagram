package com.andrew.liashuk.phasediagram.viewmodal


import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.ui.diagram.DiagramViewModel
import org.junit.Test
import org.junit.Assert.*




class DiagramViewModelTest {

    private val diagramViewModel = DiagramViewModel()

    @Test
    fun checkCreatedBranchesNotEmpty() {
        val phaseDiagram = PhaseData(
            1000.0,
            2000.0,
            20.0,
            30.0,
            10000.0,
            -10000.0,
            0.0,
            20000.0
        )

        val (solidEntries, liquidEntries) = diagramViewModel.createDiagramBranches(phaseDiagram)
        assertTrue("Solid branche is empty!", solidEntries.isNotEmpty())
        assertTrue("Liquid branche is empty!", liquidEntries.isNotEmpty())
    }


    @Test
    fun checkFirstMeltingTempMissedException() {
        try {
            val phaseDiagram = PhaseData(
                null,
                2000.0,
                20.0,
                30.0
            )

            diagramViewModel.createDiagramBranches(phaseDiagram)
            fail("Should have thrown IllegalArgumentException exception")
        } catch (ex: IllegalArgumentException) {
            //success
        }
    }


    @Test
    fun checkSecondMeltingTempMissedException() {
        try {
            val phaseDiagram = PhaseData(
                1000.0,
                null,
                20.0,
                30.0
            )

            diagramViewModel.createDiagramBranches(phaseDiagram)
            fail("Should have thrown IllegalArgumentException exception")
        } catch (ex: IllegalArgumentException) {
            //success
        }
    }


    @Test
    fun checkFirstEntropyMissedException() {
        try {
            val phaseDiagram = PhaseData(
                1000.0,
                2000.0,
                null,
                30.0
            )

            diagramViewModel.createDiagramBranches(phaseDiagram)
            fail("Should have thrown IllegalArgumentException exception")
        } catch (ex: IllegalArgumentException) {
            //success
        }
    }


    @Test
    fun checkSecondEntropyMissedException() {
        try {
            val phaseDiagram = PhaseData(
                1000.0,
                2000.0,
                20.0,
                null
            )

            diagramViewModel.createDiagramBranches(phaseDiagram)
            fail("Should have thrown IllegalArgumentException exception")
        } catch (ex: IllegalArgumentException) {
            //success
        }
    }
}