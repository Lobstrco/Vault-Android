import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 24
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

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    namespace = "com.lobstr.stellar.tsmapper"
}

val stellar_sdk by extra("1.5.0")
val gson by extra("2.13.1")
val firebase_bom by extra("34.1.0")

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("network.lightsail:stellar-sdk:$stellar_sdk")
    implementation("network.lightsail:stellar-sdk-android-spi:$stellar_sdk")
    implementation ("com.google.code.gson:gson:$gson")
    implementation(platform("com.google.firebase:firebase-bom:$firebase_bom"))
    implementation("com.google.firebase:firebase-crashlytics")
}