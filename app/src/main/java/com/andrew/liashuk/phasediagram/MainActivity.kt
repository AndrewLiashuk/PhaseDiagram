package com.andrew.liashuk.phasediagram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        //Fabric.with(this, Crashlytics.Builder().core(core).build())

        //Fabric.with(this, Crashlytics())
    }
}
