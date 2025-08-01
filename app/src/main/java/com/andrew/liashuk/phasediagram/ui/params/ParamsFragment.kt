package com.andrew.liashuk.phasediagram.ui.params

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.andrew.liashuk.phasediagram.R
import com.andrew.liashuk.phasediagram.common.resourceHolder
import com.andrew.liashuk.phasediagram.common.mainHandler
import com.andrew.liashuk.phasediagram.common.ext.accumulate
import com.andrew.liashuk.phasediagram.common.ext.collectWithLifecycle
import com.andrew.liashuk.phasediagram.common.ext.setSupportActionBar
import com.andrew.liashuk.phasediagram.common.ext.toPrettyString
import com.andrew.liashuk.phasediagram.databinding.FragmentParamsBinding
import com.andrew.liashuk.phasediagram.model.Elements
import com.andrew.liashuk.phasediagram.model.PhaseData
import com.andrew.liashuk.phasediagram.model.SolutionType
import com.andrew.liashuk.phasediagram.ui.utils.validation.Condition
import com.andrew.liashuk.phasediagram.ui.utils.validation.MoreThanCondition
import com.andrew.liashuk.phasediagram.ui.utils.validation.NotEmptyCondition
import com.andrew.liashuk.phasediagram.ui.utils.validation.createValidator
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParamsFragment : Fragment() {

    private val handler by mainHandler()
    private val viewModel: ParamsViewModel by viewModels()
    private var binding: FragmentParamsBinding by resourceHolder()

    private val elementsLayoutPairs: List<Pair<Elements, TextInputLayout>>
        get() = listOf(
            Elements.MELTING_TEMPERATURE_FIRST to binding.layoutFirstTemp,
            Elements.ENTROPY_FIRST to binding.layoutFirstEntropy,
            Elements.ALPHA_L_FIRST to binding.layoutFirstAlphaL,
            Elements.ALPHA_S_FIRST to binding.layoutFirstAlphaS,

            Elements.MELTING_TEMPERATURE_SECOND to binding.layoutSecondTemp,
            Elements.ENTROPY_SECOND to binding.layoutSecondEntropy,
            Elements.ALPHA_L_SECOND to binding.layoutSecondAlphaL,
            Elements.ALPHA_S_SECOND to binding.layoutSecondAlphaS,
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParamsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setSupportActionBar(binding.toolbar)

        handler.postAction(action = ::setupInputFields)
        binding.btnBuild.setOnClickListener { viewModel.onBuildClick() }
        collectUiState()
    }

    private fun collectUiState() {
        viewModel.uiState.accumulate().collectWithLifecycle(this) { (previousStep, newState) ->
            if (previousStep?.phaseData != newState.phaseData) {
                updatePhaseDataFields(newState.phaseData)
            }

            if (previousStep?.solutionType != newState.solutionType) {
                changePhaseType(newState.solutionType)
            }

            binding.btnBuild.isEnabled = newState.buildBtnEnabled

            if (newState.openDiagram) {
                openDiagramScreen(newState.phaseData)
                viewModel.onDiagramOpened()
            }
        }
    }

    private fun updatePhaseDataFields(data: PhaseData) {
        // such code is required for cases when the number of `PhaseData` fields will be updated
        for (element in Elements.entries) {
           val value = when (element) {
                Elements.MELTING_TEMPERATURE_FIRST -> data.meltingTempFirst
                Elements.ENTROPY_FIRST -> data.entropyFirst
                Elements.ALPHA_L_FIRST -> data.alphaLFirst
                Elements.ALPHA_S_FIRST -> data.alphaSFirst

                Elements.MELTING_TEMPERATURE_SECOND -> data.meltingTempSecond
                Elements.ENTROPY_SECOND -> data.entropySecond
                Elements.ALPHA_L_SECOND -> data.alphaLSecond
                Elements.ALPHA_S_SECOND -> data.alphaSSecond
            }
            updateField(element, value)
        }
    }

    private fun updateField(element: Elements, value: Double?) {
        val layout = elementsLayoutPairs.first { it.first == element }.second
        layout.editText?.let {
            val newValue = value.toPrettyString()

            if (it.text.toString() != newValue) {
                it.setText(newValue)
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_params, menu)

                // update selected item once menu is created
                val itemId = when (viewModel.uiState.value.solutionType) {
                    SolutionType.IDEAL -> R.id.menu_ideal
                    SolutionType.REGULAR -> R.id.menu_regular
                    SolutionType.SUBREGULAR -> R.id.menu_subregular
                }
                menu.findItem(itemId).isChecked = true
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_ideal -> viewModel.changeType(SolutionType.IDEAL)

                    R.id.menu_regular -> viewModel.changeType(SolutionType.REGULAR)

                    R.id.menu_subregular -> viewModel.changeType(SolutionType.SUBREGULAR)

                    R.id.menu_sample -> viewModel.sampleData()

                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupInputFields() {
        val validators = elementsLayoutPairs.map { (element, editText) ->
            val validator = editText.createValidator(this, *createConditions(element)) { text ->
                viewModel.updatePhaseData(element, text)
            }
            element to validator
        }.toTypedArray()

        viewModel.addValidator(*validators)
    }

    private fun createConditions(element: Elements): Array<Pair<Condition, String>> {
        val notEmpty = NotEmptyCondition()
        val moreThanZero by lazy { MoreThanCondition(value = 0.0) }

        return when(element) {
            Elements.MELTING_TEMPERATURE_FIRST -> arrayOf(
                notEmpty to getString(R.string.empty_first_temp),
                moreThanZero to getString(R.string.small_first_temp),
            )
            Elements.ENTROPY_FIRST -> arrayOf(
                notEmpty to getString(R.string.empty_first_entropy),
                moreThanZero to getString(R.string.small_first_entropy),
            )
            Elements.ALPHA_L_FIRST -> arrayOf(notEmpty to getString(R.string.empty_alpha_l))
            Elements.ALPHA_S_FIRST -> arrayOf(notEmpty to getString(R.string.empty_alpha_s))

            Elements.MELTING_TEMPERATURE_SECOND -> arrayOf(
                notEmpty to getString(R.string.empty_second_temp),
                moreThanZero to getString(R.string.small_second_temp),
            )
            Elements.ENTROPY_SECOND -> arrayOf(
                notEmpty to getString(R.string.empty_second_entropy),
                moreThanZero to getString(R.string.small_second_entropy),
            )
            Elements.ALPHA_L_SECOND -> arrayOf(notEmpty to getString(R.string.empty_alpha_l))
            Elements.ALPHA_S_SECOND -> arrayOf(notEmpty to getString(R.string.empty_alpha_s))
        }
    }

    private fun openDiagramScreen(phaseData: PhaseData) {
        val action = ParamsFragmentDirections.actionParamsFragmentToDiagramFragment(phaseData)
        findNavController().navigate(action)
    }

    private fun changePhaseType(type: SolutionType) = with(binding) {
        when (type) {
            SolutionType.IDEAL -> {
                groupFirstAlphas.isVisible = false
                groupSecondAlphas.isVisible = false
                toolbar.menu.findItem(R.id.menu_ideal)?.isChecked = true
            }
            SolutionType.REGULAR -> {
                groupFirstAlphas.isVisible = true
                groupSecondAlphas.isVisible = false
                toolbar.menu.findItem(R.id.menu_regular)?.isChecked = true
                changeAlphaFieldsPosition(centerFirstGroup = true)
            }
            SolutionType.SUBREGULAR -> {
                groupFirstAlphas.isVisible = true
                groupSecondAlphas.isVisible = true
                toolbar.menu.findItem(R.id.menu_subregular)?.isChecked = true
                changeAlphaFieldsPosition(centerFirstGroup = false)
            }
        }
    }

    private fun changeAlphaFieldsPosition(centerFirstGroup: Boolean) = with(ConstraintSet()) {
        clone(binding.layoutCard)

        val startId = if (centerFirstGroup) R.id.guideline_third_first else ConstraintSet.PARENT_ID
        val endId = if (centerFirstGroup) R.id.guideline_third_second else R.id.guideline_half

        // change AlphaL field position
        connect(R.id.layout_first_alpha_l, ConstraintSet.START, startId, ConstraintSet.START, 0)
        connect(R.id.layout_first_alpha_l, ConstraintSet.END, endId, ConstraintSet.START, 0)

        // change AlphaS field position
        connect(R.id.layout_first_alpha_s, ConstraintSet.START, startId, ConstraintSet.START, 0)
        connect(R.id.layout_first_alpha_s, ConstraintSet.END, endId, ConstraintSet.START, 0)

        applyTo(binding.layoutCard)
    }
}