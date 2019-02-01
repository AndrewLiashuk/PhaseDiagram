package com.andrew.liashuk.phasediagram

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.andrew.liashuk.phasediagram.databinding.DiagramFragmentBinding
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.ui.CustomMarkerView
import com.andrew.liashuk.phasediagram.viewmodal.DiagramViewModel
import com.crashlytics.android.Crashlytics
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.diagram_fragment.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class DiagramFragment : Fragment(), CoroutineScope {

    private lateinit var viewModel: DiagramViewModel
    private lateinit var binding: DiagramFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.diagram_fragment, container, false)
    }


    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DiagramViewModel::class.java)
        val phaseData = DiagramFragmentArgs.fromBundle(arguments!!).phaseData
        //val phaseData = PhaseData(1000.0, 2000.0, 20.0, 30.0)

        binding = DataBindingUtil.setContentView(activity!!, R.layout.diagram_fragment)

        setupPlot()
        setPlotData(phaseData)
    }


    private fun setupPlot() {
        with(binding.chart) {
            setDrawGridBackground(false)
            description.isEnabled = false // no description text
            setTouchEnabled(true) // enable touch gestures
            isDragEnabled = true // enable scaling and dragging
            setScaleEnabled(true)
            setPinchZoom(true)  // if disabled, scaling can be done on x- and y-axis separately

            val mv = CustomMarkerView(activity!!, R.layout.custom_marker_view)
            mv.chartView = this // For bounds control
            marker = mv // Set the marker to the chart

            val xl = xAxis
            xl.position = XAxis.XAxisPosition.BOTTOM
            xl.axisMinimum = 0f
            xl.axisMaximum = 100f
        }
    }


    private fun setPlotData(phaseData: PhaseData?) = launch {
        if (phaseData == null) {
            Crashlytics.getInstance().core.logException(Exception("Phase data is null."))
            Toast.makeText(activity, "Please try again.", Toast.LENGTH_SHORT).show()
            view!!.findNavController().popBackStack()
            return@launch
        }

        binding.chart.data = createDiagramDataAsync(phaseData).await()
        binding.chart.invalidate()
        binding.chart.animateX(1000)

        binding.progressBar.visibility = View.GONE
    }


    private fun createDiagramDataAsync(phaseData: PhaseData): Deferred<LineData> =
        async(Dispatchers.Default) {
            createDiagramData(phaseData)
        }


    private fun createDiagramData(phaseData: PhaseData): LineData {
        val (solidEntries, liquidEntries) = viewModel.createDiagramBranches(phaseData)

        val liquidDataSet = LineDataSet(liquidEntries, "Liquid")
        liquidDataSet.lineWidth = 1.5f
        liquidDataSet.setDrawCircles(false)

        val solidDataSet = LineDataSet(solidEntries, "Solid")
        solidDataSet.color = ContextCompat.getColor(activity!!, R.color.redColor)
        solidDataSet.lineWidth = 1.5f
        solidDataSet.setDrawCircles(false)

        return LineData(liquidDataSet, solidDataSet)
    }

/*    private fun saveToGallery(chart: Chart<*>, name: String) {
        if (chart.saveToGallery(name + "_" + System.currentTimeMillis(), 70))
            Toast.makeText(
                applicationContext, "Saving SUCCESSFUL!",
                Toast.LENGTH_SHORT
            ).show()
        else
            Toast.makeText(applicationContext, "Saving FAILED!", Toast.LENGTH_SHORT)
                .show()
    }*/
}
