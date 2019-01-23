package com.andrew.liashuk.phasediagram

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
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
