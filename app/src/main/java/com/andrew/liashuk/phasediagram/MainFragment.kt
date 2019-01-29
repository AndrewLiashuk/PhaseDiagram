package com.andrew.liashuk.phasediagram

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.andrew.liashuk.phasediagram.types.PhaseData
import kotlinx.android.synthetic.main.main_fragment.*


class MainFragment : Fragment() {



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
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        /*fab.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToDiagramFragment()
            action.phaseData = PhaseData(1000.0, 2000.0, 20.0, 30.0, 10000.0, 0.0)
            it.findNavController().navigate(action)
        }*/
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
}

/*
      Crashlytics.getInstance().core.setString("Key", "val")
      Crashlytics.getInstance().core.log(Log.ERROR, "TestTag", "Log2")
      Crashlytics.getInstance().core.logException(Exception("New error"))
*/
