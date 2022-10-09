package com.andrew.liashuk.buildsrc

internal const val kotlinVersion = "1.7.10"

object Libraries {

    internal object Versions {
        const val appcompat = "1.5.1"
        const val core = "1.9.0"
        const val fragment = "1.5.3"
        const val constraintLayout = "2.1.4"
        const val cardView = "1.0.0"
        const val lifecycle = "2.5.1"
        const val lifecycleExtensions = "2.2.0"
        const val navigation = "2.5.2"
        const val coroutines = "1.6.4"
        const val material = "1.6.1"
        const val hilt = "2.43.1"

        const val mpChart = "v3.1.0"
        const val firebaseBom = "30.5.0"
    }

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
        const val core = "androidx.core:core-ktx:${Versions.core}"
        const val fragment = "androidx.fragment:fragment-ktx:${Versions.fragment}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val cardView = "androidx.cardview:cardview:${Versions.cardView}"
        const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
        const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
        const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleExtensions}"
        const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
        const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    }

    const val material = "com.google.android.material:material:${Versions.material}"
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltCompiler = "com.google.dagger:hilt-compiler:${Versions.hilt}"

    const val mpAndroidChart = "com.github.PhilJay:MPAndroidChart:${Versions.mpChart}"
    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBom}"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
}

object TestLibraries {

    internal object Versions {
        const val junit4 = "4.13.2"
        const val testRunner = "1.4.0"
        const val espresso = "3.4.0"
    }

    const val junit4 = "junit:junit:${Versions.junit4}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Libraries.Versions.coroutines}"
    const val testRunner = "androidx.test:runner:${Versions.testRunner}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}