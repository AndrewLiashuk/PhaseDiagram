package com.andrew.liashuk.phasediagram

import android.Manifest
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.provider.MediaStore


class DiagramFragment : Fragment(), CoroutineScope {

    companion object {
        const val PERMISSIONS_REQUEST = 1
    }

    private lateinit var mViewModel: DiagramViewModel
    private lateinit var mBinding: DiagramFragmentBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.diagram_fragment, container, false)
    }


    // start all coroutines in UI thread
    private val mJob = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mJob


    override fun onDestroy() {
        super.onDestroy()
        // cancel all coroutines on fragment destroy
        coroutineContext.cancelChildren()
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_diagram, menu)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(activity!!, R.layout.diagram_fragment)
        mBinding.graphToolbar.title = "Diagram" // TODO
        (activity as? AppCompatActivity)?.setSupportActionBar(mBinding.graphToolbar)

        mViewModel = ViewModelProviders.of(this).get(DiagramViewModel::class.java)
        val phaseData = DiagramFragmentArgs.fromBundle(arguments!!).phaseData

        setupPlot()
        setPlotData(phaseData)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                view?.findNavController()?.popBackStack()
                true
            }
            R.id.menu_save -> {
                if (checkPermission()) {
                    saveDiagram()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun setupPlot() {
        with(mBinding.chart) {
            setDrawGridBackground(false)
            description.isEnabled = false // no description text
            setTouchEnabled(true) // enable touch gestures
            isDragEnabled = true // enable scaling and dragging
            setScaleEnabled(true)
            setPinchZoom(true)  // if disabled, scaling can be done on x- and y-axis separately

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisMinimum = 0f
            xAxis.axisMaximum = 100f

            val mv = CustomMarkerView(activity!!, R.layout.custom_marker_view)
            mv.chartView = this // For bounds control
            marker = mv // Set the marker to the chart
        }
    }


    private fun setPlotData(phaseData: PhaseData?) = launch {
        if (phaseData == null) {
            Crashlytics.getInstance().core.logException(Exception("Phase data is null."))
            Toast.makeText(activity, activity!!.getString(R.string.try_again), Toast.LENGTH_SHORT).show()
            view?.findNavController()?.popBackStack()
            return@launch
        }

        mBinding.chart.data = createDiagramDataAsync(phaseData).await()
        mBinding.chart.invalidate()
        mBinding.chart.animateX(1000)

        mBinding.groupDiagram.visibility = View.VISIBLE
        mBinding.progressBar.visibility = View.GONE
    }


    private fun createDiagramDataAsync(phaseData: PhaseData): Deferred<LineData> =
        // TODO add max time corutin

        async(Dispatchers.Default) {
            createDiagramData(phaseData)
        }


    private fun createDiagramData(phaseData: PhaseData): LineData {
        val (solidEntries, liquidEntries) = mViewModel.createDiagramBranches(phaseData)

        val liquidDataSet = LineDataSet(liquidEntries, activity!!.getString(R.string.diagram_liquid))
        liquidDataSet.color = ContextCompat.getColor(activity!!, R.color.colorPrimary)
        liquidDataSet.lineWidth = 1.5f
        liquidDataSet.setDrawCircles(false)

        val solidDataSet = LineDataSet(solidEntries, activity!!.getString(R.string.diagram_solid))
        solidDataSet.color = ContextCompat.getColor(activity!!, R.color.colorAccent)
        solidDataSet.lineWidth = 1.5f
        solidDataSet.setDrawCircles(false)

        return LineData(liquidDataSet, solidDataSet)
    }


    private fun saveDiagram() = launch {
        try {
            createAndSaveBitmapAsync().await()

            Toast.makeText(
                activity!!,
                "Image saved in gallery", // TODO
                Toast.LENGTH_SHORT
            ).show()
        } catch (ex: Exception) {
            Toast.makeText(
                activity!!,
                "Saving FAILED!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun createAndSaveBitmapAsync() = async(Dispatchers.Default) {
        createAndSaveBitmap()
    }


    private fun createAndSaveBitmap() {
        val bitmap = Bitmap.createBitmap(
            mBinding.diagramLayout.width,
            mBinding.diagramLayout.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(bitmap)
        c.drawColor(Color.WHITE)
        mBinding.diagramLayout.draw(c)

        MediaStore.Images.Media.insertImage(
            activity!!.contentResolver,
            bitmap,
            "Diagram_image", // TODO
            "Diagram image build in PhaseDiagram app"
        )
    }


    private fun checkPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return if (permission != PackageManager.PERMISSION_GRANTED) { // Permission is not granted
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST
            )

            false
        } else {
            true //Permission has already been granted
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    saveDiagram()
                } else {
                    // TODO
                    Toast.makeText(activity!!, "Without storage permission can\\'t export photos!", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {}
        }
    }
}
