package com.andrew.liashuk.phasediagram.viewmodal

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.andrew.liashuk.phasediagram.R
import com.andrew.liashuk.phasediagram.common.DispatcherProvider
import com.andrew.liashuk.phasediagram.common.Event
import com.andrew.liashuk.phasediagram.common.ResourceResolver
import com.andrew.liashuk.phasediagram.common.showProgress
import com.andrew.liashuk.phasediagram.common.showToast
import com.andrew.liashuk.phasediagram.ext.firstNotNull
import com.andrew.liashuk.phasediagram.ext.getMutableStateFlow
import com.andrew.liashuk.phasediagram.ext.isNotEmpty
import com.andrew.liashuk.phasediagram.ext.runCoroutine
import com.andrew.liashuk.phasediagram.logic.PhaseDiagramCalc
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class DiagramViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val resourceResolver: ResourceResolver,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private var documentIsRequested = false

    // TODO for ui events that should be done only once
    private val _uiEvents = Channel<Event>(Channel.BUFFERED)
    val uiEvents: Flow<Event> = _uiEvents.receiveAsFlow()

    // TODO create action only if ui is active
    private val _createDocument = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val createDocument: SharedFlow<String> = _createDocument.asSharedFlow()

    private val _diagramData: MutableStateFlow<DiagramData?> =
        savedStateHandle.getMutableStateFlow(KEY_DIAGRAM_DATA, initialValue = null)
    val diagramData: Flow<DiagramData> = _diagramData.asStateFlow().filterNotNull()

    /**
     * Calculate and store diagram points.
     *
     * @param phaseData     Input class that contains variables for diagram calculation.
     *
     * @return              Pair of ArrayList with Entrys. First is solid diagram data and second
     *                      liquid diagram data.
     *
     * @throws Exception    Throw exception if input phaseData doesn't contain meltingTempFirst or
     *                      meltingTempSecond or entropFirst or entropSecond.
     */
    fun createDiagramBranches(phaseData: PhaseData) = runCoroutine(dispatcherProvider.default()) {
        if (_diagramData.isNotEmpty()) return@runCoroutine // return DiagramData if exist
        _uiEvents.showProgress(show = true)

        val phaseDiagram = PhaseDiagramCalc(
            phaseData.meltingTempFirst ?: throw IllegalArgumentException("First melting temperature not set!"),
            phaseData.meltingTempSecond ?: throw IllegalArgumentException("Second melting temperature not set!"),
            phaseData.entropFirst ?: throw IllegalArgumentException("First entropy not set!"),
            phaseData.entropSecond ?: throw IllegalArgumentException("Second entropy not set!"),
            phaseData.alphaLFirst ?: 0.0, // if not set 0 for ideal formula
            phaseData.alphaSFirst ?: 0.0,
            phaseData.alphaLSecond ?: -1.0, // if not set -1 for regular formula
            phaseData.alphaSSecond ?: -1.0
        )

        val points = phaseDiagram.calculatePhaseDiagram()
        val solidEntries = ArrayList<Entry>(points.size)
        val liquidEntries = ArrayList<Entry>(points.size)

        // divide collection for liquid and solid
        for ((solid, liquid, temperature) in points) {
            solidEntries.add(Entry(solid.toFloat(), temperature.toFloat()))
            liquidEntries.add(Entry(liquid.toFloat(), temperature.toFloat()))
        }

        _diagramData.value = DiagramData(liquidEntries = liquidEntries, solidEntries = solidEntries)
        _uiEvents.showProgress(show = false)
    }

    fun saveDiagram() {
        if (!documentIsRequested) {
            documentIsRequested = true
            _createDocument.tryEmit(resourceResolver.getString(R.string.saved_image_name))
        }
    }

    fun setDiagramUri(uri: Uri?) = runCoroutine {
        documentIsRequested = false

        if (uri != null) {
            // save document only after diagram data calculation
            _diagramData.firstNotNull()

            _uiEvents.trySend(SaveDocument(uri))
            _uiEvents.showProgress(show = true)

        } else {
            _uiEvents.showToast(resourceResolver.getString(R.string.permissions_not_grant))
        }
    }

    fun setSaveDiagramResult(result: Result<Boolean>) {
        var messageRes: Int? = null

        result.fold(
            onSuccess = {
                messageRes = if (it) R.string.successful_image_save else R.string.failed_image_save
            },
            onFailure = {
                messageRes = if (it is TimeoutCancellationException) R.string.long_time_save else R.string.failed_image_save
            }
        )

        _uiEvents.showToast(resourceResolver.getString(messageRes!!))
        _uiEvents.showProgress(show = false)
    }

    companion object {
        private const val KEY_DIAGRAM_DATA = "diagram_data"
    }
}