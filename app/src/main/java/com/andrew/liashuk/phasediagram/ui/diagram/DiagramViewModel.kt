package com.andrew.liashuk.phasediagram.ui.diagram

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.andrew.liashuk.phasediagram.R
import com.andrew.liashuk.phasediagram.common.DispatcherProvider
import com.andrew.liashuk.phasediagram.common.Event
import com.andrew.liashuk.phasediagram.common.ResourceResolver
import com.andrew.liashuk.phasediagram.common.ext.firstNotNull
import com.andrew.liashuk.phasediagram.common.ext.getMutableStateFlow
import com.andrew.liashuk.phasediagram.common.ext.isEmpty
import com.andrew.liashuk.phasediagram.common.ext.runCoroutine
import com.andrew.liashuk.phasediagram.common.ext.showProgress
import com.andrew.liashuk.phasediagram.common.ext.showToast
import com.andrew.liashuk.phasediagram.common.ext.withProgress
import com.andrew.liashuk.phasediagram.domain.DiagramCalculator
import com.andrew.liashuk.phasediagram.model.DiagramData
import com.andrew.liashuk.phasediagram.model.PhaseData
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

    // The state can be lost, this flag just prevents multiple clicking
    private var documentIsRequested = false

    // Stores events until someone collects them. Analog of SingleLiveEvent
    private val _uiEvents = Channel<Event>(Channel.BUFFERED)
    val uiEvents: Flow<Event> = _uiEvents.receiveAsFlow()

    // Emits event and remove it immediately. Even if there is no collector event will be deleted. It can be used in rare cases.
    private val _createDocument = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val createDocument: SharedFlow<String> = _createDocument.asSharedFlow()

    private val _diagramData: MutableStateFlow<DiagramData?> =
        savedStateHandle.getMutableStateFlow(KEY_DIAGRAM_DATA, initialValue = null)
    val diagramData: Flow<DiagramData> = _diagramData.asStateFlow().filterNotNull()

    fun setPhaseData(phaseData: PhaseData) {
        if (_diagramData.isEmpty()) {
            _uiEvents.withProgress {
                createDiagram(phaseData)
            }
        }
    }

    private fun createDiagram(phaseData: PhaseData) = runCoroutine(dispatcherProvider.default()) {
        val phaseDiagram = DiagramCalculator(phaseData)
        val points = phaseDiagram.build()

        val solidEntries = ArrayList<Entry>(points.size)
        val liquidEntries = ArrayList<Entry>(points.size)

        // divide collection for liquid and solid
        for ((solid, liquid, temperature) in points) {
            solidEntries.add(Entry(solid.toFloat(), temperature.toFloat()))
            liquidEntries.add(Entry(liquid.toFloat(), temperature.toFloat()))
        }

        _diagramData.value = DiagramData(liquidEntries = liquidEntries, solidEntries = solidEntries)
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