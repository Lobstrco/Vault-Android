// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
        classpath("com.google.gms:google-services:4.3.5")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.5.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.32-alpha")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://zendesk.jfrog.io/zendesk/repo") }
        maven { url = uri("https://jitpack.io") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
