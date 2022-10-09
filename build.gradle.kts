buildscript {
    // import doesn't work for the buildscript block
    val gradlePlugins = com.andrew.liashuk.buildsrc.GradlePlugins

    dependencies {
        classpath(gradlePlugins.android)
        classpath(gradlePlugins.kotlin)
        classpath(gradlePlugins.safeArgs)
        classpath(gradlePlugins.hilt)
        classpath(gradlePlugins.crashlytics)
        classpath(gradlePlugins.gms)
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
