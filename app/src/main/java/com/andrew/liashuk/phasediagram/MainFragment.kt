package com.andrew.liashuk.phasediagram

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.andrew.liashuk.phasediagram.databinding.MainFragmentBinding
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.types.SolutionType
import com.andrew.liashuk.phasediagram.helpers.Helpers
import icepick.Icepick
import icepick.State


class MainFragment : Fragment() {

    @State @JvmField
    var mPhaseData = PhaseData() // dataBinging automatically update data
    @State @JvmField
    var mPhaseType = SolutionType.SUBREGULAR

    private lateinit var mBinding: MainFragmentBinding
    private var mSubregularMenuItem: MenuItem? = null // set checked on sample menu click


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Icepick.restoreInstanceState(this, savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(requireActivity(), R.layout.main_fragment)
        mBinding.phaseData = mPhaseData
        mBinding.mainFragment = this

        (activity as? AppCompatActivity)?.setSupportActionBar(mBinding.toolbar)

        changePhaseType(mPhaseType) // update UI by phase type
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        mSubregularMenuItem = menu?.findItem(R.id.menu_subregular)

        when(mPhaseType) {
            SolutionType.IDEAL -> {
                menu.findItem(R.id.menu_ideal).isChecked = true
            }
            SolutionType.REGULAR -> {
                menu.findItem(R.id.menu_regular).isChecked = true
            }
            SolutionType.SUBREGULAR -> {
                mSubregularMenuItem?.isChecked = true
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_ideal -> {
                item.isChecked = true
                changePhaseType(SolutionType.IDEAL)
                true
            }
            R.id.menu_regular -> {
                item.isChecked = true
                changePhaseType(SolutionType.REGULAR)
                true
            }
            R.id.menu_subregular -> {
                item.isChecked = true
                changePhaseType(SolutionType.SUBREGULAR)
                true
            }
            R.id.menu_sample -> { // set sample data
                mSubregularMenuItem?.isChecked = true
                changePhaseType(SolutionType.SUBREGULAR)
                mPhaseData = PhaseData(1000.0, 1300.0, 30.0, 20.0, 20000.0, 0.0, 10000.0, -10000.0)
                mBinding.phaseData = mPhaseData
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun onBuildClick() {
        try {
            val errorTextId = mPhaseData.checkData(mPhaseType)

            if (errorTextId != null) {
                Helpers.showAlert(activity, errorTextId)
            } else {
                val action = MainFragmentDirections.actionMainFragmentToDiagramFragment()
                action.phaseData = mPhaseData
                view!!.findNavController().navigate(action)
            }
        } catch (ex: Exception) {
            //Crashlytics.getInstance().core.logException(ex)
            Helpers.showErrorAlert(activity, ex)
        }
    }

    /**
     *  Depending on the solution type show or hide some alpha editTexts
     */
    private fun changePhaseType(type: SolutionType) {
        try {
            mPhaseType = type
            mPhaseData.changeType(type)

            when(type) {
                SolutionType.IDEAL -> {
                    mBinding.groupFirstAlphas.visibility = View.GONE
                    mBinding.groupSecondAlphas.visibility = View.GONE
                }
                SolutionType.REGULAR -> {
                    mBinding.groupFirstAlphas.visibility = View.VISIBLE
                    mBinding.groupSecondAlphas.visibility = View.GONE
                    changeAlphaEditPosition(true)
                }
                SolutionType.SUBREGULAR -> {
                    mBinding.groupFirstAlphas.visibility = View.VISIBLE
                    mBinding.groupSecondAlphas.visibility = View.VISIBLE
                    changeAlphaEditPosition(false)
                }
            }
        } catch (ex: Exception) {
            //Crashlytics.getInstance().core.logException(ex)
            Helpers.showErrorAlert(activity, ex)
        }
    }

    /**
     * @param isRegular     <code>true</code> center alphaL and alphaS textViews by set constraint
     *                      params and place between guideline_third_first and guideline_third_second
     *                      <code>false</code> return to initial constraint params
     */
    private fun changeAlphaEditPosition(isRegular: Boolean) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(mBinding.cardConstraintLayout)

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

        constraintSet.applyTo(mBinding.cardConstraintLayout)
    }
}
