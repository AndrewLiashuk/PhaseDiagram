import com.andrew.liashuk.buildsrc.AndroidSdk
import com.andrew.liashuk.buildsrc.Libraries
import com.andrew.liashuk.buildsrc.TestLibraries

plugins {
    // import doesn't work for the plugins block
    val plugins = com.andrew.liashuk.buildsrc.Plugins

    id(plugins.application)
    id(plugins.kotlinAndroid)
    id(plugins.kotlinKapt)
    id(plugins.kotlinParcelize)
    id(plugins.hilt)
    id(plugins.safeargs)
    id(plugins.crashlytics)
}

android {
    compileSdk = AndroidSdk.compile

    defaultConfig {
        applicationId = AndroidSdk.applicationId
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xcontext-receivers",
        )
    }

    buildFeatures {
        viewBinding = true
    }

    bundle {
        storeArchive {
            enable = false
        }
    }
}

dependencies {
    implementation(Libraries.AndroidX.appcompat)
    implementation(Libraries.AndroidX.core)
    implementation(Libraries.AndroidX.fragment)
    implementation(Libraries.AndroidX.constraintLayout)
    implementation(Libraries.AndroidX.cardView)
    implementation(Libraries.AndroidX.lifecycleRuntime)
    implementation(Libraries.AndroidX.lifecycleViewModel)
    implementation(Libraries.AndroidX.lifecycleExtensions)
    implementation(Libraries.AndroidX.navigationFragment)
    implementation(Libraries.AndroidX.navigationUi)

    implementation(Libraries.Kotlin.stdlib)
    implementation(Libraries.Kotlin.coroutinesCore)
    implementation(Libraries.Kotlin.coroutinesAndroid)

    implementation(Libraries.material)
    implementation(Libraries.hiltAndroid)
    kapt(Libraries.hiltCompiler)

    implementation(Libraries.mpAndroidChart)

    // Import the BoM for the Firebase platform
    //implementation platform("com.google.firebase:firebase-bom:30.1.0")
    //implementation("com.google.firebase:firebase-crashlytics")

    testImplementation(TestLibraries.junit4)
    testImplementation(TestLibraries.coroutinesTest)

    androidTestImplementation(TestLibraries.testRunner)
    androidTestImplementation(TestLibraries.espresso)
}
