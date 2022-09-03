package com.andrew.liashuk.phasediagram.viewmodal

import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.types.SolutionType

data class ParamsUiState(
    val phaseData: PhaseData = PhaseData(),
    val solutionType: SolutionType = SolutionType.SUBREGULAR,
    val buildBtnEnabled: Boolean = true,
    val openDiagram: Boolean = false,
)