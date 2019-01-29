package com.andrew.liashuk.phasediagram.viewmodal

import androidx.lifecycle.ViewModel
import com.andrew.liashuk.phasediagram.logic.PhaseDiagramCalc
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.github.mikephil.charting.data.Entry


class DiagramViewModel : ViewModel() {

    private var mDiagramData: Pair<ArrayList<Entry>, ArrayList<Entry>>? = null


    fun createDiagramBranches(phaseData: PhaseData): Pair<ArrayList<Entry>, ArrayList<Entry>> {
        if (mDiagramData != null) {
            return mDiagramData!!
        }

        val phaseDiagram = PhaseDiagramCalc(phaseData)
        val points = phaseDiagram.calculatePhaseDiagram()

        val solidEntries = ArrayList<Entry>(points.size)
        val liquidEntries = ArrayList<Entry>(points.size)

        points.map {
            solidEntries.add(Entry(it.solid.toFloat(), it.temperature.toFloat()))
            liquidEntries.add(Entry(it.liquid.toFloat(), it.temperature.toFloat()))
        }

        mDiagramData = Pair(solidEntries, liquidEntries)
        return mDiagramData!!
    }
}
