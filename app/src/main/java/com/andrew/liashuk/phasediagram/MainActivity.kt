package com.andrew.liashuk.phasediagram

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.andrew.liashuk.phasediagram.logic.CustomMarkerView
import com.andrew.liashuk.phasediagram.logic.PhaseDiagramCalc
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setupPlot()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
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


        val mv = CustomMarkerView(this, R.layout.custom_marker_view)
        mv.chartView = chart // For bounds control
        chart.marker = mv // Set the marker to the chart

        val xl = chart.xAxis
        xl.position = XAxis.XAxisPosition.BOTTOM
        xl.axisMinimum = 0f
        xl.axisMaximum = 100f

        setData()

        // redraw
        chart.invalidate()
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

        // create a data object with the data sets
        val data = LineData(liquidDataSet, solidDataSet)
        chart.data = data
    }


    /*
    private PlotModel CreatePlot(PhaseDiagramCalc newDiagram)
        {
            PlotModel plotModel = new PlotModel { Title = "Phase diagram" };
            var series1 = new LineSeries { Title = "Liquid" }; // MarkerType = MarkerType.Circle
            var series2 = new LineSeries { Title = "Solid" };

            foreach (var point in newDiagram.Points)
            {
                series1.Points.Add(new DataPoint(point.Solid, point.Temperature));
                series2.Points.Add(new DataPoint(point.Liquid, point.Temperature));
            }

            plotModel.Series.Add(series1);
            plotModel.Series.Add(series2);
            plotModel.Axes.Add(new LinearAxis { Position = AxisPosition.Bottom, Title = "%" });
            plotModel.Axes.Add(new LinearAxis { Position = AxisPosition.Left, Title = "T" });// Minimum = 1700, Maximum = 1900

            return plotModel;
        }
     */
}
