package com.andrew.liashuk.phasediagram.ui.params

import androidx.lifecycle.SavedStateHandle
import com.andrew.liashuk.phasediagram.CoroutineTestRule
import com.andrew.liashuk.phasediagram.model.Elements
import com.andrew.liashuk.phasediagram.model.SolutionType
import com.andrew.liashuk.phasediagram.ui.utils.validation.Validator
import com.andrew.liashuk.phasediagram.ui.utils.validation.ValidatorImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ParamsViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val savedStateHandle = SavedStateHandle()

    private val viewModel by lazy { ParamsViewModel(savedStateHandle = savedStateHandle) }

    @Test
    fun `should trigger validation if it was previously`() = coroutineTestRule.runTest {
        savedStateHandle[ParamsViewModel.KEY_UI_STATE] = ParamsUiState(buildBtnEnabled = false)

        val validators = createValidators()
        viewModel.addValidator(*validators.toList().toTypedArray())

        assertTrue(validators.values.all { it.isActive })
    }

    @Test
    fun `should update button status on phase data update`() = coroutineTestRule.runTest {
        val validators = createValidators()
        viewModel.addValidator(*validators.toList().toTypedArray())

        viewModel.onBuildClick()
        assertFalse(getUiState().buildBtnEnabled)

        // validate all validators
        validators.values.forEach { it.validate(null) }

        viewModel.updatePhaseData(Elements.ENTROPY_SECOND, "100")
        assertTrue(getUiState().buildBtnEnabled)
    }

    @Test
    fun `should update phase data with provided value`() = coroutineTestRule.runTest {
        viewModel.updatePhaseData(Elements.ENTROPY_SECOND, "100")
        assertEquals(getUiState().phaseData.entropySecond, 100.0)

        viewModel.updatePhaseData(Elements.ALPHA_L_SECOND, "11.11")
        assertEquals(getUiState().phaseData.alphaLSecond, 11.11)

        viewModel.updatePhaseData(Elements.MELTING_TEMPERATURE_SECOND, "0")
        assertEquals(getUiState().phaseData.meltingTempSecond, 0.0)

        viewModel.updatePhaseData(Elements.ALPHA_S_SECOND, "-1000")
        assertEquals(getUiState().phaseData.alphaSSecond, -1000.0)
    }

    @Test
    fun `should not update phase data with not valid value`() = coroutineTestRule.runTest {
        viewModel.updatePhaseData(Elements.ALPHA_S_FIRST, "")
        assertEquals(getUiState().phaseData.alphaSFirst, null)

        viewModel.updatePhaseData(Elements.MELTING_TEMPERATURE_FIRST, null)
        assertEquals(getUiState().phaseData.meltingTempFirst, null)

        viewModel.updatePhaseData(Elements.ENTROPY_FIRST, "test")
        assertEquals(getUiState().phaseData.entropyFirst, null)

        viewModel.updatePhaseData(Elements.ALPHA_L_FIRST, "1o0.0")
        assertEquals(getUiState().phaseData.alphaLFirst, null)
    }

    @Test
    fun `should disable button if validations are not valid`() = coroutineTestRule.runTest {
        val validators = createValidators()
        viewModel.addValidator(*validators.toList().toTypedArray())

        assertTrue(getUiState().buildBtnEnabled)

        viewModel.onBuildClick()
        assertFalse(getUiState().buildBtnEnabled)
        assertFalse(getUiState().openDiagram)
    }

    @Test
    fun `should open diagram screen only after validation`() = coroutineTestRule.runTest {
        val validators = createValidators()
        viewModel.addValidator(*validators.toList().toTypedArray())
        viewModel.changeType(SolutionType.REGULAR)

        viewModel.onBuildClick()
        assertFalse(getUiState().buildBtnEnabled)
        assertFalse(getUiState().openDiagram)

        // validate only active validators
        validators.filter { (_, validator) -> validator.isActive }
            .forEach { it.value.validate(null) }

        viewModel.onBuildClick()
        assertTrue(getUiState().openDiagram)
    }

    @Test
    fun `should open diagram screen on button click`() = coroutineTestRule.runTest {
        val validators = createValidators()
        // validate all validators
        validators.values.forEach { it.validate(null) }

        viewModel.addValidator(*validators.toList().toTypedArray())

        viewModel.onBuildClick()
        assertTrue(getUiState().openDiagram)
    }

    @Test
    fun `should mark that diagram is opened`() = coroutineTestRule.runTest {
        val validators = createValidators()
        // validate all validators
        validators.values.forEach { it.validate(null) }

        viewModel.addValidator(*validators.toList().toTypedArray())

        viewModel.onBuildClick()
        assertTrue(getUiState().openDiagram)

        viewModel.onDiagramOpened()
        assertFalse(getUiState().openDiagram)
    }

    // TODO test changeType

    @Test
    fun `should provide sample data`() = coroutineTestRule.runTest {
        // change default solution type
        viewModel.changeType(SolutionType.REGULAR)
        assertEquals(getUiState().solutionType, SolutionType.REGULAR)

        viewModel.sampleData()

        val phaseData = getUiState().phaseData
        assertEquals(phaseData.meltingTempFirst, 1000.0)
        assertEquals(phaseData.meltingTempSecond, 1300.0)
        assertEquals(phaseData.entropyFirst, 30.0)
        assertEquals(phaseData.entropySecond, 20.0)
        assertEquals(phaseData.alphaLFirst, 20000.0)
        assertEquals(phaseData.alphaSFirst, 0.0)
        assertEquals(phaseData.alphaLSecond, 10000.0)
        assertEquals(phaseData.alphaSSecond, -10000.0)

        assertEquals(getUiState().solutionType, SolutionType.SUBREGULAR)
    }


    private fun createValidators() : Map<Elements, Validator> =
        Elements.values().associateWith { ValidatorImpl(emptyList()) }

    private suspend fun getUiState(): ParamsUiState = viewModel.uiState.first()
}