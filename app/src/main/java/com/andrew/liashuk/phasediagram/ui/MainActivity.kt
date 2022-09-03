package com.andrew.liashuk.phasediagram.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andrew.liashuk.phasediagram.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        //Fabric.with(this, Crashlytics.Builder().core(core).build())

        //Fabric.with(this, Crashlytics())
    }
}
