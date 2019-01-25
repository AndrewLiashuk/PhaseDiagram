package com.andrew.liashuk.phasediagram

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


class GraphFragment : Fragment() {

    private lateinit var viewModel: GraphViewModel


    companion object {
        fun newInstance() = GraphFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.graph_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GraphViewModel::class.java)
    }


    private fun setupPlot() {
        chart.setDrawGridBackground(false)

        // no description text
        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(true)

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true)

        chart.animateX(1000)

        val mv = CustomMarkerView(activity!!, R.layout.custom_marker_view)
        mv.chartView = chart // For bounds control
        chart.marker = mv // Set the marker to the chart

        val xl = chart.xAxis
        xl.position = XAxis.XAxisPosition.BOTTOM
        xl.axisMinimum = 0f
        xl.axisMaximum = 100f

        setData()
    }


    private fun setData() {
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
        solidDataSet.color = ContextCompat.getColor(activity!!, R.color.redColor)
        solidDataSet.lineWidth = 1.5f
        solidDataSet.setDrawCircles(false)

        chart.data = LineData(liquidDataSet, solidDataSet)
        chart.invalidate()
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
