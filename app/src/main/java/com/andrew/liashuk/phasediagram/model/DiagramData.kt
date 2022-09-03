package com.andrew.liashuk.phasediagram.model

import android.os.Parcelable
import com.github.mikephil.charting.data.Entry
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiagramData(
    val liquidEntries: ArrayList<Entry>,
    val solidEntries: ArrayList<Entry>,
) : Parcelable