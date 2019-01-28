package com.andrew.liashuk.phasediagram

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.andrew.liashuk.phasediagram.logic.PhaseDiagramCalc
import com.andrew.liashuk.phasediagram.ui.CustomMarkerView
import com.andrew.liashuk.phasediagram.viewmodal.GraphViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.graph_fragment.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class GraphFragment : Fragment(), CoroutineScope {

    private lateinit var viewModel: GraphViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.graph_fragment, container, false)
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
        viewModel = ViewModelProviders.of(this).get(GraphViewModel::class.java)
        val phaseData = GraphFragmentArgs.fromBundle(arguments!!).phaseData
        setupPlot()
    }


    private fun setupPlot() {
        with(chart) {
            setDrawGridBackground(false)
            description.isEnabled = false // no description text
            setTouchEnabled(true) // enable touch gestures
            isDragEnabled = true // enable scaling and dragging
            setScaleEnabled(true)
            setPinchZoom(true)  // if disabled, scaling can be done on x- and y-axis separately
            animateX(1000)
        }

        val mv = CustomMarkerView(activity!!, R.layout.custom_marker_view)
        mv.chartView = chart // For bounds control
        chart.marker = mv // Set the marker to the chart

        val xl = chart.xAxis
        xl.position = XAxis.XAxisPosition.BOTTOM
        xl.axisMinimum = 0f
        xl.axisMaximum = 100f

        launch {
            val data = getPlotDataAsync().await()
            chart.data = data
            chart.invalidate()

            progressBar.visibility = View.GONE
        }
    }


    private fun getPlotDataAsync(): Deferred<LineData> = async(Dispatchers.Default) {
        getPlotData(activity!!.applicationContext)
    }


    private fun getPlotData(context: Context): LineData {
        val phaseDiagram = PhaseDiagramCalc(
            1000.0,
            2000.0,
            20.0,
            30.0
        )

        val points = phaseDiagram.calculatePhaseDiagram()

        val solidEntries = ArrayList<Entry>(points.size)
        val liquidEntries = ArrayList<Entry>(points.size)

        points.map {
            solidEntries.add(Entry(it.solid.toFloat(), it.temperature.toFloat()))
            liquidEntries.add(Entry(it.liquid.toFloat(), it.temperature.toFloat()))
        }

        val liquidDataSet = LineDataSet(liquidEntries, "Liquid")
        liquidDataSet.lineWidth = 1.5f
        liquidDataSet.setDrawCircles(false)

        val solidDataSet = LineDataSet(solidEntries, "Solid")
        solidDataSet.color = ContextCompat.getColor(context, R.color.redColor)
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
