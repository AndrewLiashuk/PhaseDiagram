package com.andrew.liashuk.buildsrc

object AndroidSdk {
    const val min = 21
    const val compile = 33
    const val target = compile
    const val applicationId = "com.andrew.liashuk.phasediagram"
}

object Plugins {

    const val application = "com.android.application"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinKapt = "kotlin-kapt"
    const val kotlinParcelize = "kotlin-parcelize"
    const val hilt = "dagger.hilt.android.plugin"
    const val safeargs = "androidx.navigation.safeargs"
    const val crashlytics = "com.google.firebase.crashlytics"
    const val gms = "com.google.gms.google-services"
}

object GradlePlugins {

    internal object Versions {
        const val buildToolsVersion = "7.3.0"
        const val safeArgsVersion = "2.5.2"
        const val hiltVersion = "2.38.1"
        const val crashlyticsVersion = "2.9.2"
        const val gms = "4.3.3"
    }

    const val android = "com.android.tools.build:gradle:${Versions.buildToolsVersion}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.safeArgsVersion}"
    const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hiltVersion}"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsVersion}"
    const val gms = "com.google.gms:google-services:${Versions.gms}"
}