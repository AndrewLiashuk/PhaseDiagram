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
import com.andrew.liashuk.phasediagram.ext.collectWithLifecycle
import com.andrew.liashuk.phasediagram.ext.onLaidOut
import com.andrew.liashuk.phasediagram.ext.runCoroutine
import com.andrew.liashuk.phasediagram.ext.setSupportActionBar
import com.andrew.liashuk.phasediagram.ext.showToast
import com.andrew.liashuk.phasediagram.ui.CustomMarkerView
import com.andrew.liashuk.phasediagram.viewmodal.DiagramData
import com.andrew.liashuk.phasediagram.viewmodal.DiagramViewModel
import com.andrew.liashuk.phasediagram.common.HideProgress
import com.andrew.liashuk.phasediagram.common.ShowProgress
import com.andrew.liashuk.phasediagram.common.ShowToast
import com.andrew.liashuk.phasediagram.viewmodal.SaveDocument
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

@AndroidEntryPoint
class DiagramFragment : Fragment() {

    private val viewModel: DiagramViewModel by viewModels()
    private var binding: DiagramFragmentBinding by resourceHolder()

    private var createDocument = registerForActivityResult(
        ActivityResultContracts.CreateDocument(mimeType = MIME_TYPE)
    ) { uri: Uri? -> viewModel.setDiagramUri(uri) }

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

        // TODO improve
        val phaseData = DiagramFragmentArgs.fromBundle(requireArguments()).phaseData
        viewModel.createDiagramBranches(phaseData)

        collectViewModel(firstStart = savedInstanceState == null)
        setupChart()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_diagram, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> findNavController().popBackStack()

                    R.id.menu_save -> viewModel.saveDiagram()

                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun collectViewModel(firstStart: Boolean) {
        viewModel.createDocument.collectWithLifecycle(this) { name ->
            createDocument.launch(name)
        }
        viewModel.diagramData.collectWithLifecycle(this, Lifecycle.State.CREATED) {
            setChartData(it, animate = firstStart)
        }
        viewModel.uiEvents.collectWithLifecycle(this, Lifecycle.State.RESUMED) { event ->
            when (event) {
                is ShowProgress, is HideProgress ->
                    binding.progressBar.isVisible = event is ShowProgress

                is ShowToast -> requireContext().showToast(event.message)

                is SaveDocument -> saveDiagramIfReady(event.uri)
            }
        }
    }

    private fun setupChart() = with(binding.lineChart) {
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

    private fun setChartData(diagramData: DiagramData, animate: Boolean) = with(binding) {
        lineChart.data = LineData(
            createDataSet(
                entries = diagramData.liquidEntries,
                label = getString(R.string.diagram_liquid),
                color = MaterialColors.getColor(requireView(), R.attr.colorPrimary)
            ),
            createDataSet(
                entries = diagramData.solidEntries,
                label = getString(R.string.diagram_solid),
                color = MaterialColors.getColor(requireView(), R.attr.colorAccent)
            )
        )
        lineChart.invalidate()
        if (animate) lineChart.animateX(1000)
        binding.groupDiagram.isVisible = true
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

    private fun saveDiagramIfReady(uri: Uri) {
        // TODO add comment
        if (ViewCompat.isLaidOut(binding.layoutDiagram)) {
            saveDiagram(uri)
        } else {
            binding.layoutDiagram.onLaidOut {
                saveDiagram(uri)
            }
        }
    }

    private fun saveDiagram(uri: Uri) = runCoroutine {
        val bitmap = Bitmap.createBitmap(
            binding.layoutDiagram.measuredWidth,
            binding.layoutDiagram.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val result = runCatching {
            withTimeout(SAVE_TIMEOUT) { saveBitmap(bitmap, uri) }
        }
        viewModel.setSaveDiagramResult(result)
        bitmap.recycle()
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
