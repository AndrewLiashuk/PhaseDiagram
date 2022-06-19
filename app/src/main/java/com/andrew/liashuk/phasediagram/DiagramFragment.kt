package com.andrew.liashuk.phasediagram

import android.Manifest
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.andrew.liashuk.phasediagram.databinding.DiagramFragmentBinding
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.ui.CustomMarkerView
import com.andrew.liashuk.phasediagram.viewmodal.DiagramViewModel
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
import androidx.navigation.fragment.findNavController
import com.andrew.liashuk.phasediagram.ext.setSupportActionBar
import com.andrew.liashuk.phasediagram.helpers.Helpers

class DiagramFragment : Fragment(), CoroutineScope {

    // start all coroutines in UI thread
    private val mJob = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mJob

    private lateinit var mViewModel: DiagramViewModel

    private var _binding: DiagramFragmentBinding? = null
    private val binding: DiagramFragmentBinding
        get() = checkNotNull(_binding) { "Binding property is only valid after onCreateView and before onDestroyView are called." }

    private var mBuildDiagram = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DiagramFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_diagram, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = getString(R.string.diagram_fragment_title)
        setSupportActionBar(binding.toolbar)

        mViewModel = ViewModelProviders.of(this).get(DiagramViewModel::class.java)
        val phaseData = DiagramFragmentArgs.fromBundle(requireArguments()).phaseData

        setupPlot()
        setPlotData(phaseData)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            R.id.menu_save -> {
                if (mBuildDiagram && checkPermission()) {
                    saveDiagram()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupPlot() = with(binding.lineChart) {
        setDrawGridBackground(false)
        description.isEnabled = false // no description text
        setTouchEnabled(true) // enable touch gestures
        isDragEnabled = true // enable scaling and dragging
        setScaleEnabled(true)
        setPinchZoom(true)  // if disabled, scaling can be done on x- and y-axis separately

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 100f

        marker = CustomMarkerView(requireContext(), R.layout.custom_marker_view).also {
            it.chartView = this // For bounds control
        }
    }

    private fun setPlotData(phaseData: PhaseData) = launch {
        try {
            withTimeout(10000L) {
                binding.lineChart.data = createDiagramData(phaseData)
            }
            binding.lineChart.invalidate()
            binding.lineChart.animateX(1000)

            mBuildDiagram = true
            binding.groupDiagram.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        } catch (timout: TimeoutCancellationException) {
            //Crashlytics.getInstance().core.logException(timout)
            Helpers.showErrorAlert(activity, R.string.long_time_calculation)
        } catch (ex: Exception) {
            //Crashlytics.getInstance().core.logException(ex)
            Helpers.showErrorAlert(activity, ex)
        }
    }

    private suspend fun createDiagramData(phaseData: PhaseData): LineData = withContext(Dispatchers.Default) {
        val (solidEntries, liquidEntries) = mViewModel.createDiagramBranches(phaseData)

        val liquidDataSet = LineDataSet(liquidEntries, getString(R.string.diagram_liquid)).apply {
            color = ContextCompat.getColor(requireActivity(), R.color.colorPrimary)
            lineWidth = 1.5f
            setDrawCircles(false)
        }

        val solidDataSet = LineDataSet(solidEntries, getString(R.string.diagram_solid)).apply {
            color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
            lineWidth = 1.5f
            setDrawCircles(false)
        }

        LineData(liquidDataSet, solidDataSet)
    }

    private fun saveDiagram() = launch {
        try {
            binding.progressBar.visibility = View.VISIBLE
            withTimeout(10000L) {
                createAndSaveBitmap()
            }
            Helpers.showToast(activity, R.string.successful_image_save)

        } catch (timout: TimeoutCancellationException) {
            //Crashlytics.getInstance().core.logException(timout)
            Helpers.showErrorAlert(activity, R.string.long_time_save)
        } catch (ex: Exception) {
            //Crashlytics.getInstance().core.logException(ex)
            Helpers.showToast(activity, R.string.failed_image_save)
        } finally {
            binding.progressBar.visibility = View.GONE
        }
    }

    private suspend fun createAndSaveBitmap() = withContext(Dispatchers.Default) {
        val bitmap = Bitmap.createBitmap(
            binding.layoutDiagram.width,
            binding.layoutDiagram.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(bitmap)
        c.drawColor(Color.WHITE)
        binding.layoutDiagram.draw(c)

        MediaStore.Images.Media.insertImage(
            requireActivity().contentResolver,
            bitmap,
            getString(R.string.saved_image_name),
            getString(R.string.saved_image_desc)
        )
    }

    private fun checkPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                    Helpers.showToast(activity, R.string.permissions_not_grant)
                }
            }
            else -> {}
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST = 1
    }
}
