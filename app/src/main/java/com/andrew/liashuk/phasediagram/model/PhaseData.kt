package com.andrew.liashuk.phasediagram.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PhaseData(
    val meltingTempFirst: Double? = null,
    val meltingTempSecond: Double? = null,
    val entropyFirst: Double? = null,
    val entropySecond: Double? = null,
    val alphaLFirst: Double? = null,
    val alphaSFirst: Double? = null,
    val alphaLSecond: Double? = null,
    val alphaSSecond: Double? = null
) : Parcelable

fun PhaseData.copy(element: Elements, value: Double?): PhaseData {
    return PhaseData(
        meltingTempFirst = if (Elements.MELTING_TEMPERATURE_FIRST == element) value else meltingTempFirst,
        meltingTempSecond = if (Elements.MELTING_TEMPERATURE_SECOND == element) value else meltingTempSecond,
        entropyFirst = if (Elements.ENTROPY_FIRST == element) value else entropyFirst,
        entropySecond = if (Elements.ENTROPY_SECOND == element) value else entropySecond,
        alphaLFirst = if (Elements.ALPHA_L_FIRST == element) value else alphaLFirst,
        alphaSFirst = if (Elements.ALPHA_S_FIRST == element) value else alphaSFirst,
        alphaLSecond = if (Elements.ALPHA_L_SECOND == element) value else alphaLSecond,
        alphaSSecond = if (Elements.ALPHA_S_SECOND == element) value else alphaSSecond,
    )
}

enum class Elements {
    MELTING_TEMPERATURE_FIRST,
    ENTROPY_FIRST,
    ALPHA_L_FIRST,
    ALPHA_S_FIRST,

    MELTING_TEMPERATURE_SECOND,
    ENTROPY_SECOND,
    ALPHA_L_SECOND,
    ALPHA_S_SECOND,
}