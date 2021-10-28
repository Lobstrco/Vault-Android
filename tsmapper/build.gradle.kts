plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 16
        targetSdk = 30
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isJniDebuggable = true
        }

        getByName("release") {
            isMinifyEnabled = false
            isJniDebuggable = false
        }
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.github.stellar:java-stellar-sdk:0.29.0")
    implementation ("com.google.code.gson:gson:2.8.8")
    implementation("androidx.annotation:annotation:1.2.0")
}