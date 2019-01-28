package com.andrew.liashuk.phasediagram.types

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class PhaseData(
    val meltingTempFirst: Double,
    val meltingTempSecond: Double,
    val entropFirst: Double,
    val entropSecond: Double,
    val alphaLFirst: Double = 0.0,
    val alphaSFirst: Double = 0.0,
    val alphaLSecond: Double = -1.0, // if -1 use regular formula
    val alphaSSecond: Double = -1.0
) : Parcelable