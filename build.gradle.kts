buildscript {
    dependencies {
        classpath ("com.android.tools.build:gradle:7.3.0")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.0")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
        classpath ("com.google.firebase:firebase-crashlytics-gradle:2.9.0")
    }

    repositories {
        google()
        mavenCentral()
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")// MPAndroidChart
    }
}

tasks.register("clean").configure {
    delete("build")
}
