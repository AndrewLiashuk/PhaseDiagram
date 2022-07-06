package com.andrew.liashuk.phasediagram.viewmodal

import androidx.lifecycle.ViewModel
import com.andrew.liashuk.phasediagram.MainFragment
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.types.SolutionType
import com.andrew.liashuk.phasediagram.types.setValueByElement
import com.andrew.liashuk.phasediagram.ui.validation.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.EnumMap
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private var phaseData = PhaseData()
    private var phaseType: SolutionType = SolutionType.SUBREGULAR

    private val validators: EnumMap<MainFragment.Elements, Validator> = EnumMap(MainFragment.Elements::class.java)

    fun addValidator(element: MainFragment.Elements, validator: Validator) {
        validators[element] = validator
    }

    fun updatePhaseData(element: MainFragment.Elements, value: String?) {
        phaseData.setValueByElement(element, value?.toDoubleOrNull())
    }

    fun onBuildClick() {

    }

    fun changeType(phaseType: SolutionType) {


    }

    fun showSmaple() {
        // for example
        // PhaseData(1000.0, 1300.0, 30.0, 20.0, 20000.0, 0.0, 10000.0, -10000.0)

        changeType(SolutionType.SUBREGULAR)
    }
}