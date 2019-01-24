package com.andrew.liashuk.phasediagram

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.andrew.liashuk.phasediagram.ui.CustomMarkerView
import com.andrew.liashuk.phasediagram.logic.PhaseDiagramCalc
import com.crashlytics.android.Crashlytics
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        Fabric.with(this, Crashlytics())

        setupPlot()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
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

        val mv = CustomMarkerView(this, R.layout.custom_marker_view)
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
        solidDataSet.color = ContextCompat.getColor(this, R.color.redColor)
        solidDataSet.lineWidth = 1.5f
        solidDataSet.setDrawCircles(false)

        chart.data = LineData(liquidDataSet, solidDataSet)
        chart.invalidate()
    }


    private fun saveToGallery(chart: Chart<*>, name: String) {
        if (chart.saveToGallery(name + "_" + System.currentTimeMillis(), 70))
            Toast.makeText(
                applicationContext, "Saving SUCCESSFUL!",
                Toast.LENGTH_SHORT
            ).show()
        else
            Toast.makeText(applicationContext, "Saving FAILED!", Toast.LENGTH_SHORT)
                .show()
    }


    /*
            Crashlytics.getInstance().core.setString("Key", "val")
            Crashlytics.getInstance().core.log(Log.ERROR, "TestTag", "Log2")
            Crashlytics.getInstance().core.logException(Exception("New error"))
     */
}
