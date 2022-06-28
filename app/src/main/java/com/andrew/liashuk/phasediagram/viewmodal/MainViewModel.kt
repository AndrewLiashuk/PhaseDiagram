package com.andrew.liashuk.phasediagram.viewmodal

import androidx.lifecycle.ViewModel
import com.andrew.liashuk.phasediagram.types.PhaseData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    var mPhaseData = PhaseData()

    fun updatePhaseData(test: PhaseData.() -> Unit) {
        // TODO
    }

}