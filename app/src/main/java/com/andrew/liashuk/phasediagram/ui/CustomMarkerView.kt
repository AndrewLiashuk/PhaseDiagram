package com.andrew.liashuk.phasediagram.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView

import com.andrew.liashuk.phasediagram.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils

@SuppressLint("ViewConstructor")
class CustomMarkerView(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e == null) {
            return
        }

        if (e is CandleEntry) {
            tvContent.text = Utils.formatNumber(e.high, 0, true, ' ')
        } else {
            tvContent.text = Utils.formatNumber(e.y, 0, true, ' ')
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}