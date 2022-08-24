package com.andrew.liashuk.phasediagram.viewmodal

import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.types.SolutionType

data class MainUiState(
    val solutionType: SolutionType = SolutionType.SUBREGULAR,
    val openDiagram: Boolean = false,
    val phaseData: PhaseData = PhaseData()
)