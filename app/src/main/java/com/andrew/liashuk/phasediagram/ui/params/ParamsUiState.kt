package com.andrew.liashuk.phasediagram.ui.params

import android.os.Parcelable
import com.andrew.liashuk.phasediagram.model.PhaseData
import com.andrew.liashuk.phasediagram.model.SolutionType
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParamsUiState(
    val phaseData: PhaseData = PhaseData(),
    val solutionType: SolutionType = SolutionType.SUBREGULAR,
    val buildBtnEnabled: Boolean = true,
    val openDiagram: Boolean = false,
) : Parcelable