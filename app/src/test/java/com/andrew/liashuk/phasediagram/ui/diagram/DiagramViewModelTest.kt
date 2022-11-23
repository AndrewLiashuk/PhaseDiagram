package com.andrew.liashuk.phasediagram.ui.diagram

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.andrew.liashuk.phasediagram.CoroutineTestRule
import com.andrew.liashuk.phasediagram.DispatcherProviderMock
import com.andrew.liashuk.phasediagram.R
import com.andrew.liashuk.phasediagram.ResourceResolverMock
import com.andrew.liashuk.phasediagram.assertInstanceOf
import com.andrew.liashuk.phasediagram.common.HideProgress
import com.andrew.liashuk.phasediagram.common.ShowProgress
import com.andrew.liashuk.phasediagram.common.ShowToast
import com.andrew.liashuk.phasediagram.model.DiagramData
import com.andrew.liashuk.phasediagram.model.PhaseData
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DiagramViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val savedStateHandle = SavedStateHandle()

    private val viewModel by lazy {
        DiagramViewModel(
            savedStateHandle = savedStateHandle,
            resourceResolver = ResourceResolverMock(),
            dispatcherProvider = DispatcherProviderMock(coroutineTestRule.testDispatcher),
        )
    }

    @Test
    fun `should calculate diagram data after setting phase data`() = coroutineTestRule.runTest {
        viewModel.uiEvents.test {
            viewModel.setPhaseData(phaseDataExample)

            assertInstanceOf<ShowProgress>(actual = awaitItem())

            val data = viewModel.diagramData.first()
            assertTrue(data.liquidEntries.isNotEmpty())
            assertTrue(data.solidEntries.isNotEmpty())

            assertInstanceOf<HideProgress>(actual = awaitItem())
        }
    }

    @Test
    fun `should restore diagram data from savedStateHandle`() = coroutineTestRule.runTest {
        savedStateHandle[DiagramViewModel.KEY_DIAGRAM_DATA] = DiagramData(ArrayList(), ArrayList())

        // should be ignored because diagramData was restored
        viewModel.setPhaseData(phaseDataExample)

        val data = viewModel.diagramData.first()
        assertTrue(data.liquidEntries.isEmpty())
        assertTrue(data.solidEntries.isEmpty())
    }

    @Test
    fun `should request to create document with given name`() = coroutineTestRule.runTest {
        viewModel.createDocument.test {
            viewModel.saveDiagram()

            assertEquals(R.string.saved_image_name.toString(), awaitItem())
        }
    }

    @Test
    fun `should request to create document only once on multiple calls`() = coroutineTestRule.runTest {
        viewModel.createDocument.test {
            viewModel.saveDiagram()
            assertEquals(R.string.saved_image_name.toString(), awaitItem())

            // request save diagram again
            viewModel.saveDiagram()
            expectNoEvents()
        }
    }

    @Test
    fun `should request to create document again after setting diagram uri`() = coroutineTestRule.runTest {
        viewModel.createDocument.test {
            var saved = false
            // first request to save diagram
            viewModel.saveDiagram()
            assertEquals(R.string.saved_image_name.toString(), awaitItem())

            launch(StandardTestDispatcher()) {
                saved = true
                // set uri to reset save diagram process
                viewModel.setDiagramUri(uri = null)
                // third request to save diagram
                viewModel.saveDiagram()
            }

            assertFalse("The third request happened before the second", saved)
            // second request to save diagram
            viewModel.saveDiagram()

            // the second request should be ignored because after the first request, uri didn't come
            assertEquals(R.string.saved_image_name.toString(), awaitItem())
            assertTrue("The event should come only after setting new diagram uri", saved)
        }
    }

    @Test
    fun `should show toast when uri is missed`() = coroutineTestRule.runTest {
        viewModel.setDiagramUri(uri = null)

        val event = viewModel.uiEvents.first()
        assertInstanceOf<ShowToast>(actual = event)
        assertEquals(R.string.permissions_not_grant.toString(), event.message)
    }

    @Test
    fun `should send an event to save document with provided uri`() = coroutineTestRule.runTest {
        // document can be saved only when the diagram data are calculated
        savedStateHandle[DiagramViewModel.KEY_DIAGRAM_DATA] = DiagramData(ArrayList(), ArrayList())

        val uri = mockk<Uri>()
        viewModel.setDiagramUri(uri)
        viewModel.uiEvents.test {
            val event = awaitItem()
            assertInstanceOf<SaveDocument>(actual = event)
            assertEquals(uri, event.uri)

            assertInstanceOf<ShowProgress>(actual = awaitItem())
        }
    }

    @Test
    fun `should save diagram only after diagram data calculation`() = coroutineTestRule.runTest {
        var diagramDataCalculated = false
        viewModel.setDiagramUri(mockk())

        launch(StandardTestDispatcher()) {
            diagramDataCalculated = true
            viewModel.setPhaseData(phaseDataExample)
        }

        assertFalse(
            "Calculations should happen only after collection uiEvents",
            diagramDataCalculated
        )

        viewModel.uiEvents.test {
            // the first event comes from setPhaseData
            skipItems(1)

            assertTrue(
                "The SaveDocument should come only after the diagram data calculation",
                diagramDataCalculated
            )
            assertInstanceOf<SaveDocument>(actual = awaitItem())
            assertInstanceOf<ShowProgress>(actual = awaitItem())
        }
    }

    @Test
    fun `should show toast on success save diagram`() = coroutineTestRule.runTest {
        viewModel.setSaveDiagramResult(Result.success(true))
        viewModel.uiEvents.test {
            val event = awaitItem()
            assertInstanceOf<ShowToast>(actual = event)
            assertEquals(R.string.successful_image_save.toString(), event.message)

            assertInstanceOf<HideProgress>(actual = awaitItem())
        }
    }

    @Test
    fun `should show an error toast when can't save diagram`() = coroutineTestRule.runTest {
        viewModel.setSaveDiagramResult(Result.success(false))
        viewModel.uiEvents.test {
            val event = awaitItem()
            assertInstanceOf<ShowToast>(actual = event)
            assertEquals(R.string.failed_image_save.toString(), event.message)

            assertInstanceOf<HideProgress>(actual = awaitItem())
        }
    }

    @Test
    fun `should show an error toast on exception during saving diagram`() = coroutineTestRule.runTest {
        viewModel.setSaveDiagramResult(Result.failure(Exception()))
        viewModel.uiEvents.test {
            val event = awaitItem()
            assertInstanceOf<ShowToast>(actual = event)
            assertEquals(R.string.failed_image_save.toString(), event.message)

            assertInstanceOf<HideProgress>(actual = awaitItem())
        }
    }

    @Test
    fun `should show an error toast when saving is too long`() = coroutineTestRule.runTest {
        // TimeoutCancellationException can't be created due to an internal constructor
        val exception = runCatching { withTimeout(0) {} }.exceptionOrNull()!!

        viewModel.setSaveDiagramResult(Result.failure(exception))
        viewModel.uiEvents.test {
            val event = awaitItem()
            assertInstanceOf<ShowToast>(actual = event)
            assertEquals(R.string.long_time_save.toString(), event.message)

            assertInstanceOf<HideProgress>(actual = awaitItem())
        }
    }

    companion object {
        private val phaseDataExample = PhaseData(1000.0, 1000.0, 1.0, 1.0)
    }
}