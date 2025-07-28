package com.andrew.liashuk.phasediagram.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.andrew.liashuk.phasediagram.common.ext.getWindowInsets
import com.andrew.liashuk.phasediagram.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.setWindowInsets()
    }

    private fun ActivityMainBinding.setWindowInsets() {
        root.getWindowInsets { insets ->
            statusBarLayout.updateLayoutParams { height = insets.top }
            updatePadding(
                left = insets.left,
                right = insets.right,
                bottom = insets.bottom
            )
        }
    }
}
