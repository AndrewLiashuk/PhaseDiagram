package com.andrew.liashuk.phasediagram.viewmodal

import androidx.lifecycle.ViewModel
import com.andrew.liashuk.phasediagram.logic.PhaseDiagramCalc
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.github.mikephil.charting.data.Entry


class DiagramViewModel : ViewModel() {

    private var mDiagramData: Pair<ArrayList<Entry>, ArrayList<Entry>>? = null

    /**
     * Calculate and store diagram points.
     *
     * @param phaseData     Input class that contains variables for diagram calculation.
     *
     * @return              Pair of ArrayList with Entrys. First is solid diagram data and second
     *                      liquid diagram data.
     *
     * @throws Exception    Throw exception if input phaseData doesn't contain meltingTempFirst or
     *                      meltingTempSecond or entropFirst or entropSecond.
     */
    fun createDiagramBranches(phaseData: PhaseData): Pair<ArrayList<Entry>, ArrayList<Entry>> {
        if (mDiagramData != null) { // return DiagramData if exist
            return mDiagramData!!
        }

        val phaseDiagram = PhaseDiagramCalc(
            phaseData.meltingTempFirst ?: throw Exception("First melting temperature not set!"),
            phaseData.meltingTempSecond ?: throw Exception("Second melting temperature not set!"),
            phaseData.entropFirst ?: throw Exception("First entropy not set!"),
            phaseData.entropSecond ?: throw Exception("Second entropy not set!"),
            phaseData.alphaLFirst ?: 0.0, // if not set 0 for ideal formula
            phaseData.alphaSFirst ?: 0.0,
            phaseData.alphaLSecond ?: -1.0, // if not set -1 for regular formula
            phaseData.alphaSSecond ?: -1.0
        )

        val points = phaseDiagram.calculatePhaseDiagram()
        val solidEntries = ArrayList<Entry>(points.size)
        val liquidEntries = ArrayList<Entry>(points.size)

        // divide collection for liquid and solid
        points.map {
            solidEntries.add(Entry(it.solid.toFloat(), it.temperature.toFloat()))
            liquidEntries.add(Entry(it.liquid.toFloat(), it.temperature.toFloat()))
        }

        mDiagramData = Pair(solidEntries, liquidEntries)
        return mDiagramData!!
    }
}
