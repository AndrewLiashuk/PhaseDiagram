package com.andrew.liashuk.phasediagram.ui.params

import androidx.lifecycle.ViewModel
import com.andrew.liashuk.phasediagram.model.Elements
import com.andrew.liashuk.phasediagram.model.PhaseData
import com.andrew.liashuk.phasediagram.model.SolutionType
import com.andrew.liashuk.phasediagram.model.copy
import com.andrew.liashuk.phasediagram.ui.utils.validation.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.EnumMap
import javax.inject.Inject

private typealias ValidatorMap = EnumMap<Elements, Validator>

@HiltViewModel
class ParamsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ParamsUiState())
    val uiState: StateFlow<ParamsUiState> = _uiState.asStateFlow()

    private val validators: ValidatorMap = EnumMap(Elements::class.java)

    private val ValidatorMap.isActive: Boolean
        get() = this.values.any { it.isActive }

    private val ValidatorMap.isValid: Boolean
        get() = this.values.filter { it.isActive }.all { it.isValid }

    fun addValidator(vararg validatorElementPair: Pair<Elements, Validator>) {
        for ((element, validator) in validatorElementPair) {
            validators[element] = validator
        }

        if (_uiState.value.buildBtnEnabled.not()) {
            // update validators status if it was triggered
            onBuildClick()
        }
    }

    fun updatePhaseData(element: Elements, value: String?) {
        updateBuildBtnState()

        _uiState.update { state ->
            state.copy(
                phaseData = state.phaseData.copy(
                    element = element,
                    value = value?.toDoubleOrNull()
                )
            )
        }
    }

    fun onBuildClick() {
        if (validators.isActive) {
            if (validators.isValid) {
                 // TODO add extra check of phaseData consistency
                _uiState.update { it.copy(openDiagram = true) }
            } else {
                updateBuildBtnState()
            }
        } else {
            // activate all validators
            validators.values.forEach { it.start() }
            // diactivate validators that is not needed for such solution type
            updateValidators(uiState.value.solutionType)
            // check if can go next
            onBuildClick()
        }
    }

    fun onDiagramOpened() = _uiState.update { state ->
        state.copy(openDiagram = false)
    }

    fun changeType(solutionType: SolutionType) {
        _uiState.update { it.copy(solutionType = solutionType) }
        updateValidators(solutionType)
    }

    fun sampleData() {
        changeType(SolutionType.SUBREGULAR)

        _uiState.update { state ->
            state.copy(
                phaseData = PhaseData(
                    meltingTempFirst = 1000.0,
                    meltingTempSecond = 1300.0,
                    entropyFirst = 30.0,
                    entropySecond = 20.0,
                    alphaLFirst = 20000.0,
                    alphaSFirst = 0.0,
                    alphaLSecond = 10000.0,
                    alphaSSecond = -10000.0,
                )
            )
        }
    }

    private fun updateValidators(solutionType: SolutionType) {
        if (validators.isActive) {
            // activate all validators, unnecessary validators will stop below
            validators.values.filter { !it.isActive }.forEach { it.start() }
        }

        for (element in getInactiveElementsByType(solutionType)) {
            validators[element]?.stop()
            updatePhaseData(element, null)
        }

        updateBuildBtnState()
    }

    private fun getInactiveElementsByType(solutionType: SolutionType): List<Elements> {
        return when (solutionType) {
            SolutionType.IDEAL -> listOf(
                Elements.ALPHA_L_FIRST,
                Elements.ALPHA_S_FIRST,
                Elements.ALPHA_L_SECOND,
                Elements.ALPHA_S_SECOND
            )

            SolutionType.REGULAR -> listOf(
                Elements.ALPHA_L_SECOND,
                Elements.ALPHA_S_SECOND
            )

            SolutionType.SUBREGULAR -> emptyList()
        }
    }

    private fun updateBuildBtnState() {
        if (validators.isActive) {
            _uiState.update { it.copy(buildBtnEnabled = validators.isValid) }
        }
    }
}