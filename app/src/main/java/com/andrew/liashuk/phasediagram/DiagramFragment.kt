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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.andrew.liashuk.phasediagram.common.resourceHolder
import com.andrew.liashuk.phasediagram.databinding.DiagramFragmentBinding
import com.andrew.liashuk.phasediagram.ext.onLaidOut
import com.andrew.liashuk.phasediagram.ext.runCoroutine
import com.andrew.liashuk.phasediagram.ext.setSupportActionBar
import com.andrew.liashuk.phasediagram.ext.showToast
import com.andrew.liashuk.phasediagram.ui.CustomMarkerView
import com.andrew.liashuk.phasediagram.viewmodal.DiagramViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

@AndroidEntryPoint
class DiagramFragment : Fragment() {

    private val viewModel: DiagramViewModel by viewModels()
    private var binding: DiagramFragmentBinding by resourceHolder()

    private var diagramIsSaving = false

    private var documentUri: Uri? = null
    private var createDocument = registerForActivityResult(
        ActivityResultContracts.CreateDocument(mimeType = MIME_TYPE)
    ) { uri: Uri? ->
        if (uri != null) {
            documentUri = uri
        } else {
            requireContext().showToast(R.string.permissions_not_grant)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DiagramFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = getString(R.string.diagram_fragment_title)

        val phaseData = DiagramFragmentArgs.fromBundle(requireArguments()).phaseData
        val (solidEntries, liquidEntries) = viewModel.createDiagramBranches(phaseData)

        setupPlot()
        setPlotData(liquidEntries, solidEntries, animate = savedInstanceState == null)
    }

    override fun onResume() {
        super.onResume()
        saveDiagramIfNeeded()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_diagram, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
                android.R.id.home -> {
                    findNavController().popBackStack()
                    true
                }
                R.id.menu_save -> {
                    if (diagramIsSaving) {
                        diagramIsSaving = false
                        createDocument.launch(requireContext().getString(R.string.saved_image_name))
                    }
                    true
                }
                else -> false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupPlot() = with(binding.lineChart) {
        setDrawGridBackground(false)
        description.isEnabled = false // no description text
        isDragEnabled = true // enable scaling and dragging
        setTouchEnabled(true) // enable touch gestures
        setScaleEnabled(true)
        setPinchZoom(true)  // if disabled, scaling can be done on x- and y-axis separately

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 100f

        marker = CustomMarkerView(requireContext(), R.layout.custom_marker_view).also {
            it.chartView = this // For bounds control
        }
    }

    private fun setPlotData(
        liquidEntries: ArrayList<Entry>,
        solidEntries: ArrayList<Entry>, animate: Boolean
    ) = with(binding) {
        lineChart.data = LineData(
            createDataSet(
                entries = liquidEntries,
                label = getString(R.string.diagram_liquid),
                color = MaterialColors.getColor(requireView(), R.attr.colorPrimary)
            ),
            createDataSet(
                entries = solidEntries,
                label = getString(R.string.diagram_solid),
                color = MaterialColors.getColor(requireView(), R.attr.colorAccent)
            )
        )
        lineChart.invalidate()
       if (animate) lineChart.animateX(1000)

        diagramIsSaving = true
        groupDiagram.isVisible = true
        progressBar.isVisible = false
    }

    private fun createDataSet(
        entries: ArrayList<Entry>,
        label: String,
        @ColorInt color: Int
    ) = LineDataSet(entries, label).also {
        it.color = color
        it.lineWidth = 1.5f
        it.setDrawCircles(false)
    }

    private fun saveDiagramIfNeeded() {
        documentUri?.let { uri ->
            // TODO add comment
            if (ViewCompat.isLaidOut(binding.layoutDiagram)) {
                saveDiagram(uri)
            } else {
                binding.layoutDiagram.onLaidOut {
                    saveDiagram(uri)
                }
            }
        }
    }

    private fun saveDiagram(uri: Uri) = runCoroutine {
        val bitmap = Bitmap.createBitmap(
            binding.layoutDiagram.measuredWidth,
            binding.layoutDiagram.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        try {
            binding.progressBar.isVisible = true

            val result = withTimeout(SAVE_TIMEOUT) { saveBitmap(bitmap, uri) }

            val message = if (result) R.string.successful_image_save else R.string.failed_image_save
            requireContext().showToast(message)
        } catch (ex: Exception) {
            val message = if (ex is TimeoutCancellationException) R.string.long_time_save else R.string.failed_image_save
            requireContext().showToast(message)
            //Crashlytics.getInstance().core.logException(ex)
        } finally {
            diagramIsSaving = true
            documentUri = null
            bitmap.recycle()
            binding.progressBar.isVisible = false
        }
    }

    private suspend fun saveBitmap(bitmap: Bitmap, uri: Uri): Boolean {
        val canvas = Canvas(bitmap).apply { drawColor(Color.WHITE) }

        withContext(Dispatchers.Default) { binding.layoutDiagram.draw(canvas) }

        @Suppress("BlockingMethodInNonBlockingContext")
        return withContext(Dispatchers.IO) {
            requireActivity().contentResolver.openOutputStream(uri)?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            } ?: false
        }
    }

    companion object {
        private const val MIME_TYPE = "image/png"
        private const val SAVE_TIMEOUT = 10000L
    }
}
