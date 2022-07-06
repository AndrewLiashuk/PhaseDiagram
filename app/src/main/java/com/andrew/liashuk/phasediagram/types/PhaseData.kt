package com.andrew.liashuk.phasediagram.types

import android.os.Parcelable
import com.andrew.liashuk.phasediagram.MainFragment
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhaseData(
    var meltingTempFirst: Double? = null,
    var meltingTempSecond: Double? = null,
    var entropFirst: Double? = null,
    var entropSecond: Double? = null,
    var alphaLFirst: Double? = null,
    var alphaSFirst: Double? = null,
    var alphaLSecond: Double? = null,
    var alphaSSecond: Double? = null
) : Parcelable

fun PhaseData.setValueByElement(element: MainFragment.Elements, value: Double?) {
    val field = when (element) {
        MainFragment.Elements.MELTING_TEMPERATURE_FIRST -> ::meltingTempFirst
        MainFragment.Elements.ENTROP_FIRST -> ::entropFirst
        MainFragment.Elements.ALPHA_L_FIRST -> ::alphaLFirst
        MainFragment.Elements.ALPHA_S_FIRST -> ::alphaSFirst

        MainFragment.Elements.MELTING_TEMPERATURE_SECOND -> ::meltingTempSecond
        MainFragment.Elements.ENTROP_SECOND -> ::entropSecond
        MainFragment.Elements.ALPHA_L_SECOND -> ::alphaLSecond
        MainFragment.Elements.ALPHA_S_SECOND -> ::alphaSSecond
    }
    field.set(value)
}

/**
 * Show double in normal format
 *
 * 20.00 show as 20,
 * 20.10 as 20.1
 * if null, show nothing
 */
fun Double?.toNormalString(): String {
    return when (this) {
        null -> ""

        // round double by converting to long and compare with original double, if same show rounded
        this.toLong().toDouble() -> String.format("%d", this.toLong()) // show 20.0 as 20

        else -> String.format("%s", this)
    }
}