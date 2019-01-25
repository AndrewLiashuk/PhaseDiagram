package com.andrew.liashuk.phasediagram

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class MainActivity : SingleFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
    }


    override fun createFragment(): Fragment {
        return MainFragment.newInstance()
    }

    /*
       Crashlytics.getInstance().core.setString("Key", "val")
       Crashlytics.getInstance().core.log(Log.ERROR, "TestTag", "Log2")
       Crashlytics.getInstance().core.logException(Exception("New error"))
     */
}
