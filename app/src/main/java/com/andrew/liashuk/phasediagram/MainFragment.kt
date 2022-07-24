package com.andrew.liashuk.phasediagram

import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andrew.liashuk.phasediagram.common.resourceHolder
import com.andrew.liashuk.phasediagram.common.mainHandler
import com.andrew.liashuk.phasediagram.databinding.MainFragmentBinding
import com.andrew.liashuk.phasediagram.ext.setSupportActionBar
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.types.SolutionType
import com.andrew.liashuk.phasediagram.ui.validation.Condition
import com.andrew.liashuk.phasediagram.ui.validation.MoreThanCondition
import com.andrew.liashuk.phasediagram.ui.validation.NotEmptyCondition
import com.andrew.liashuk.phasediagram.ui.validation.createValidator
import com.andrew.liashuk.phasediagram.viewmodal.MainViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private val handler by mainHandler()

    private var menu: Menu by resourceHolder()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        binding.btnBuild.setOnClickListener { viewModel.onBuildClick() }
        handler.postAction(action = ::setupInputFields)
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_ideal -> {
            viewModel.changeType(SolutionType.IDEAL)
            true
        }
        R.id.menu_regular -> {
            viewModel.changeType(SolutionType.REGULAR)
            true
        }
        R.id.menu_subregular -> {
            viewModel.changeType(SolutionType.SUBREGULAR)
            true
        }
        R.id.menu_sample -> {
            // TODO
            //viewModel.showSmaple()
            navigateNext(PhaseData(1000.0, 1300.0, 30.0, 20.0, 20000.0, 0.0, 10000.0, -10000.0))
            true
        }
        else -> super.onOptionsItemSelected(item)
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

    private fun navigateNext(phaseData: PhaseData) {
        val action = MainFragmentDirections.actionMainFragmentToDiagramFragment(phaseData)
        findNavController().navigate(action)
    }

    /**
     *  Depending on the solution type show or hide some alpha editTexts
     */
    private fun changePhaseType(type: SolutionType) {
        when (type) {
            SolutionType.IDEAL -> {
                binding.groupFirstAlphas.visibility = View.GONE
                binding.groupSecondAlphas.visibility = View.GONE
            }
            SolutionType.REGULAR -> {
                binding.groupFirstAlphas.visibility = View.VISIBLE
                binding.groupSecondAlphas.visibility = View.GONE
                changeAlphaEditPosition(isRegular = true)
            }
            SolutionType.SUBREGULAR -> {
                binding.groupFirstAlphas.visibility = View.VISIBLE
                binding.groupSecondAlphas.visibility = View.VISIBLE
                changeAlphaEditPosition(isRegular = false)
            }
        }
    }

    /**
     * @param isRegular     <code>true</code> center alphaL and alphaS textViews by set constraint
     *                      params and place between guideline_third_first and guideline_third_second
     *                      <code>false</code> return to initial constraint params
     */
    private fun changeAlphaEditPosition(isRegular: Boolean) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.layoutCard)

        // change AlphaL position
        constraintSet.connect(
            R.id.layout_first_alpha_l,
            ConstraintSet.START,
            if (isRegular) R.id.guideline_third_first else ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            0
        )
        constraintSet.connect(
            R.id.layout_first_alpha_l,
            ConstraintSet.END,
            if (isRegular) R.id.guideline_third_second else R.id.guideline_half,
            ConstraintSet.START,
            0
        )

        // change AlphaS position
        constraintSet.connect(
            R.id.layout_first_alpha_s,
            ConstraintSet.START,
            if (isRegular) R.id.guideline_third_first else ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            0
        )
        constraintSet.connect(
            R.id.layout_first_alpha_s,
            ConstraintSet.END,
            if (isRegular) R.id.guideline_third_second else R.id.guideline_half,
            ConstraintSet.START,
            0
        )

        constraintSet.applyTo(binding.layoutCard)
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
