package com.andrew.liashuk.phasediagram.viewmodal

import com.github.mikephil.charting.data.Entry

data class DiagramData(
    val liquidEntries: ArrayList<Entry>,
    val solidEntries: ArrayList<Entry>,
)