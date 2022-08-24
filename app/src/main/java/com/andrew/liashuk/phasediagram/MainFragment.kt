package com.andrew.liashuk.phasediagram

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
import com.andrew.liashuk.phasediagram.common.resourceHolder
import com.andrew.liashuk.phasediagram.common.mainHandler
import com.andrew.liashuk.phasediagram.databinding.MainFragmentBinding
import com.andrew.liashuk.phasediagram.ext.collectWithLifecycle
import com.andrew.liashuk.phasediagram.ext.setSupportActionBar
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.types.SolutionType
import com.andrew.liashuk.phasediagram.ui.validation.Condition
import com.andrew.liashuk.phasediagram.ui.validation.MoreThanCondition
import com.andrew.liashuk.phasediagram.ui.validation.NotEmptyCondition
import com.andrew.liashuk.phasediagram.ui.validation.createValidator
import com.andrew.liashuk.phasediagram.viewmodal.MainUiState
import com.andrew.liashuk.phasediagram.viewmodal.MainViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val handler by mainHandler()
    private val viewModel: MainViewModel by viewModels()
    private var binding: MainFragmentBinding by resourceHolder()

    private val elementsLayoutPairs: List<Pair<Elements, TextInputLayout>> by lazy {
        listOf(
            Elements.MELTING_TEMPERATURE_FIRST to binding.layoutFirstTemp,
            Elements.ENTROP_FIRST to binding.layoutFirstEntrop,
            Elements.ALPHA_L_FIRST to binding.layoutFirstAlphaL,
            Elements.ALPHA_S_FIRST to binding.layoutFirstAlphaS,

            Elements.MELTING_TEMPERATURE_SECOND to binding.layoutSecondTemp,
            Elements.ENTROP_SECOND to binding.layoutSecondEntrop,
            Elements.ALPHA_L_SECOND to binding.layoutSecondAlphaL,
            Elements.ALPHA_S_SECOND to binding.layoutSecondAlphaS,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setSupportActionBar(binding.toolbar)

        handler.postAction(action = ::setupInputFields)
        binding.btnBuild.setOnClickListener { viewModel.onBuildClick() }
        viewModel.uiState.collectWithLifecycle(this, action = ::updateUiState)
    }

    private fun updateUiState(state: MainUiState) {
        changePhaseType(state.solutionType)

        if (state.openDiagram) {
            openDiagramScreen(state.phaseData)
            viewModel.onDiagramOpened()
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)

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
        elementsLayoutPairs.forEach { (element, editText) ->
            val validator = editText.createValidator(this, *createConditions(element)) { text ->
                viewModel.updatePhaseData(element, text)
            }
            viewModel.addValidator(element, validator)
        }
    }

    private fun createConditions(element: Elements): Array<Pair<Condition, String>> {
        val notEmpty = NotEmptyCondition()
        val moreThanZero by lazy { MoreThanCondition(value = 0.0) }

        return when(element) {
            Elements.MELTING_TEMPERATURE_FIRST -> arrayOf(
                notEmpty to getString(R.string.empty_first_temp),
                moreThanZero to getString(R.string.small_first_temp),
            )
            Elements.ENTROP_FIRST -> arrayOf(
                notEmpty to getString(R.string.empty_first_entrop),
                moreThanZero to getString(R.string.small_first_entrop),
            )
            Elements.ALPHA_L_FIRST -> arrayOf(notEmpty to getString(R.string.empty_alpha_l))
            Elements.ALPHA_S_FIRST -> arrayOf(notEmpty to getString(R.string.empty_alpha_s))

            Elements.MELTING_TEMPERATURE_SECOND -> arrayOf(
                notEmpty to getString(R.string.empty_second_temp),
                moreThanZero to getString(R.string.small_second_temp),
            )
            Elements.ENTROP_SECOND -> arrayOf(
                notEmpty to getString(R.string.empty_second_entrop),
                moreThanZero to getString(R.string.small_second_entrop),
            )
            Elements.ALPHA_L_SECOND -> arrayOf(notEmpty to getString(R.string.empty_alpha_l))
            Elements.ALPHA_S_SECOND -> arrayOf(notEmpty to getString(R.string.empty_alpha_s))
        }
    }

    private fun openDiagramScreen(phaseData: PhaseData) {
        val action = MainFragmentDirections.actionMainFragmentToDiagramFragment(phaseData)
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

    enum class Elements {
        MELTING_TEMPERATURE_FIRST,
        ENTROP_FIRST,
        ALPHA_L_FIRST,
        ALPHA_S_FIRST,

        MELTING_TEMPERATURE_SECOND,
        ENTROP_SECOND,
        ALPHA_L_SECOND,
        ALPHA_S_SECOND,
    }
}
