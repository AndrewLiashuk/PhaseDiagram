package com.andrew.liashuk.phasediagram.ui.params

import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.model.SolutionType

data class ParamsUiState(
    val phaseData: PhaseData = PhaseData(),
    val solutionType: SolutionType = SolutionType.SUBREGULAR,
    val buildBtnEnabled: Boolean = true,
    val openDiagram: Boolean = false,
)