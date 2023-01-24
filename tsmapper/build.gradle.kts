plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 16
        targetSdk = 33
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
    namespace = "com.lobstr.stellar.tsmapper"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.github.stellar:java-stellar-sdk:0.34.2")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("androidx.annotation:annotation:1.5.0")
}