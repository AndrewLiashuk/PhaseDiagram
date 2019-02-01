package com.andrew.liashuk.phasediagram.viewmodal

import androidx.lifecycle.ViewModel
import com.andrew.liashuk.phasediagram.logic.PhaseDiagramCalc
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.github.mikephil.charting.data.Entry


class DiagramViewModel : ViewModel() {

    private var mDiagramData: Pair<ArrayList<Entry>, ArrayList<Entry>>? = null


    fun createDiagramBranches(phaseData: PhaseData): Pair<ArrayList<Entry>, ArrayList<Entry>> {
        if (mDiagramData != null) { // return if exist calculated data
            return mDiagramData!!
        }

        val phaseDiagram = PhaseDiagramCalc(
            phaseData.meltingTempFirst ?: throw Exception(""), // TODO add exception
            phaseData.meltingTempSecond ?: throw Exception(""),
            phaseData.entropFirst ?: throw Exception(""),
            phaseData.entropSecond ?: throw Exception(""),
            phaseData.alphaLFirst ?: 0.0, // if 0 use ideal formula
            phaseData.alphaSFirst ?: 0.0,
            phaseData.alphaLSecond ?: -1.0, // if -1 use regular formula
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
