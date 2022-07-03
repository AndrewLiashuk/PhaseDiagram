package com.andrew.liashuk.phasediagram

import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andrew.liashuk.phasediagram.databinding.MainFragmentBinding
import com.andrew.liashuk.phasediagram.ext.setSupportActionBar
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.types.SolutionType
import com.andrew.liashuk.phasediagram.ui.validation.createValidator
import com.andrew.liashuk.phasediagram.viewmodal.MainViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val elementsLayoutPairs: List<Pair<Elements, TextInputLayout>> by lazy {
        listOf(
            Elements.MELTING_TEMPERATURE_FIRST to binding.firstTempLayout,
            Elements.ENTROP_FIRST to binding.firstEntropLayout,
            Elements.ALPHA_L_FIRST to binding.firstAlphaLLayout,
            Elements.ALPHA_S_FIRST to binding.firstAlphaSLayout,

            Elements.MELTING_TEMPERATURE_SECOND to binding.secondTempLayout,
            Elements.ENTROP_SECOND to binding.secondTempLayout,
            Elements.ALPHA_L_SECOND to binding.secondTempLayout,
            Elements.ALPHA_S_SECOND to binding.secondTempLayout,
        )
    }

    private val viewModel: MainViewModel by viewModels()

    private var _binding: MainFragmentBinding? = null
    private val binding: MainFragmentBinding
        get() = checkNotNull(_binding) { "Binding property is only valid after onCreateView and before onDestroyView are called." }

    private var menu: Menu? = null // set checked on sample menu click

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        menu = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBuild.setOnClickListener { viewModel.onBuildClick() }

        elementsLayoutPairs.forEach { (element, editText) ->
            val validator = editText.createValidator(this) { text ->
                viewModel.updatePhaseData(element, text)
            }
            viewModel.addValidator(element, validator)
        }

        setSupportActionBar(binding.toolbar)


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
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
            viewModel.showSmaple()

            navigateNext(PhaseData(1000.0, 1300.0, 30.0, 20.0, 20000.0, 0.0, 10000.0, -10000.0))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    fun navigateNext(phaseData: PhaseData) {
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
                changeAlphaEditPosition(true)
            }
            SolutionType.SUBREGULAR -> {
                binding.groupFirstAlphas.visibility = View.VISIBLE
                binding.groupSecondAlphas.visibility = View.VISIBLE
                changeAlphaEditPosition(false)
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
        constraintSet.clone(binding.cardConstraintLayout)

        // change AlphaL position
        constraintSet.connect(
            R.id.firstAlphaLLayout,
            ConstraintSet.START,
            if (isRegular) R.id.guideline_third_first else ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            0
        )
        constraintSet.connect(
            R.id.firstAlphaLLayout,
            ConstraintSet.END,
            if (isRegular) R.id.guideline_third_second else R.id.guideline_half,
            ConstraintSet.START,
            0
        )

        // change AlphaS position
        constraintSet.connect(
            R.id.firstAlphaSLayout,
            ConstraintSet.START,
            if (isRegular) R.id.guideline_third_first else ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            0
        )
        constraintSet.connect(
            R.id.firstAlphaSLayout,
            ConstraintSet.END,
            if (isRegular) R.id.guideline_third_second else R.id.guideline_half,
            ConstraintSet.START,
            0
        )

        constraintSet.applyTo(binding.cardConstraintLayout)
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
