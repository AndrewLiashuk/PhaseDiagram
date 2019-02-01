package com.andrew.liashuk.phasediagram

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.andrew.liashuk.phasediagram.databinding.MainFragmentBinding
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.types.SolutionType
import com.crashlytics.android.Crashlytics


class MainFragment : Fragment() {

    // dataBinging automatically update data
    private var mPhaseData = PhaseData()
    private var mPhaseType = SolutionType.IDEAL
    private lateinit var mBinding: MainFragmentBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(activity!!, R.layout.main_fragment)
        mBinding.phaseData = mPhaseData
        mBinding.mainFragment = this

        (activity as? AppCompatActivity)?.setSupportActionBar(mBinding.toolbar)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
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
                changePhaseType(SolutionType.SUBREGULAR)
                mPhaseData = PhaseData(1000.0, 2000.0, 20.0, 30.0, 10000.0, 0.0, -10000.0, 10000.0)
                mBinding.phaseData = mPhaseData
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun onBuildClick() {
        val action = MainFragmentDirections.actionMainFragmentToDiagramFragment()
        action.phaseData = mPhaseData
        view?.findNavController()?.navigate(action) ?: Crashlytics.getInstance().core.logException(Exception("Can't open DiagramFragment."))
    }


    private fun changePhaseType(type: SolutionType) {
        mPhaseType = type

        // TODO apply changes
    }
}

/*
      Crashlytics.getInstance().core.setString("Key", "val")
      Crashlytics.getInstance().core.log(Log.ERROR, "TestTag", "Log2")
      Crashlytics.getInstance().core.logException(Exception("New error"))
*/
