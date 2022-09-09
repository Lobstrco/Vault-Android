plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 16
        targetSdk = 32
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    namespace = "com.andrognito.pinlockview"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.core:core-ktx:1.8.0")
}