plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 16
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    namespace = "com.lobstr.stellar.tsmapper"
}

val stellar_sdk by extra("0.43.0")
val gson by extra("2.10.1")
val firebase_bom by extra("32.7.1")

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.github.stellar:java-stellar-sdk:$stellar_sdk")
    implementation("com.github.stellar:java-stellar-sdk-android-spi:$stellar_sdk")
    implementation ("com.google.code.gson:gson:$gson")
    implementation(platform("com.google.firebase:firebase-bom:$firebase_bom"))
    implementation("com.google.firebase:firebase-crashlytics")
}