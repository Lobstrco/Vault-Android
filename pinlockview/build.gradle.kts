plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(30)
        versionCode = 5
        versionName = "2.1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.core:core-ktx:1.3.2")
}