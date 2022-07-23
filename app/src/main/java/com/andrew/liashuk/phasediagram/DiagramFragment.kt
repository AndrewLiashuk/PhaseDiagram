package com.andrew.liashuk.phasediagram

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.andrew.liashuk.phasediagram.databinding.DiagramFragmentBinding
import com.andrew.liashuk.phasediagram.ext.onLaidOut
import com.andrew.liashuk.phasediagram.ext.setSupportActionBar
import com.andrew.liashuk.phasediagram.helpers.Helpers
import com.andrew.liashuk.phasediagram.types.PhaseData
import com.andrew.liashuk.phasediagram.ui.CustomMarkerView
import com.andrew.liashuk.phasediagram.viewmodal.DiagramViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

@AndroidEntryPoint
class DiagramFragment : Fragment() {

    private val viewModel: DiagramViewModel by viewModels()

    private var _binding: DiagramFragmentBinding? = null
    private val binding: DiagramFragmentBinding
        get() = checkNotNull(_binding) { "Binding property is only valid after onCreateView and before onDestroyView are called." }

    private var mBuildDiagram = false
    private var documentUri: Uri? = null
    private var createDocument = registerForActivityResult(
        ActivityResultContracts.CreateDocument(mimeType = "image/png")
    ) { uri: Uri? ->
        if (uri != null) {
            documentUri = uri
        } else {
            Helpers.showToast(activity, R.string.permissions_not_grant)
        }
    }

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

        val phaseData = DiagramFragmentArgs.fromBundle(requireArguments()).phaseData

        setupPlot()
        setPlotData(phaseData, animate = savedInstanceState == null)
    }

    override fun onResume() {
        super.onResume()

        documentUri?.let { uri ->
            if(ViewCompat.isLaidOut(binding.layoutDiagram)) {
                saveDiagram(uri)
            } else {
                binding.layoutDiagram.onLaidOut {
                    saveDiagram(uri)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            R.id.menu_save -> {
                if (mBuildDiagram) {
                    createDocument.launch(requireContext().getString(R.string.saved_image_name))
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

    private fun setPlotData(phaseData: PhaseData, animate: Boolean) {
        binding.lineChart.data = createDiagramData(phaseData)
        binding.lineChart.invalidate()
       if (animate) binding.lineChart.animateX(1000)

        mBuildDiagram = true
        binding.groupDiagram.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun createDiagramData(phaseData: PhaseData): LineData {
        val (solidEntries, liquidEntries) = viewModel.createDiagramBranches(phaseData)

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

        return LineData(liquidDataSet, solidDataSet)
    }

    private fun saveDiagram(uri: Uri) = lifecycleScope.launch {
        try {
            binding.progressBar.visibility = View.VISIBLE
            withTimeout(10000L) {
                createAndSaveBitmap(uri)
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

    private suspend fun createAndSaveBitmap(uri: Uri) {
        val bitmap = Bitmap.createBitmap(
            binding.layoutDiagram.measuredWidth,
            binding.layoutDiagram.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap).apply { drawColor(Color.WHITE) }

        withContext(Dispatchers.Default) { binding.layoutDiagram.draw(canvas) }

        @Suppress("BlockingMethodInNonBlockingContext")
        withContext(Dispatchers.IO) {
            requireActivity().contentResolver.openOutputStream(uri)?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }

        bitmap.recycle()
    }
}
