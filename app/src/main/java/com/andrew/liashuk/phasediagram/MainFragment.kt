package com.andrew.liashuk.phasediagram

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.andrew.liashuk.phasediagram.databinding.MainFragmentBinding
import com.andrew.liashuk.phasediagram.types.PhaseData


class MainFragment : Fragment() {

    // init with 0.0 value because on binding class PhaseData show 0.0 as ""
    private var mPhaseData = PhaseData(1000.0, 2000.0, 10.0, 20.0)
    private lateinit var mNavController: NavController


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

        val binding: MainFragmentBinding = DataBindingUtil.setContentView(activity!!, R.layout.main_fragment)
        binding.phaseData = mPhaseData
        binding.mainFragment = this

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.menu_ideal -> {
                item.isChecked = true
                true
            }
            R.id.menu_regular -> {
                item.isChecked = true
                true
            }
            R.id.menu_subregular -> {
                item.isChecked = true
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    fun onBuildClick() {
        val action = MainFragmentDirections.actionMainFragmentToDiagramFragment()
        action.phaseData = mPhaseData
        view!!.findNavController().navigate(action)
    }
}

/*
      Crashlytics.getInstance().core.setString("Key", "val")
      Crashlytics.getInstance().core.log(Log.ERROR, "TestTag", "Log2")
      Crashlytics.getInstance().core.logException(Exception("New error"))
*/
