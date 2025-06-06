import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 35
    defaultConfig {
        applicationId = "com.lobstr.stellar.vault"
        minSdk = 24
        targetSdk = 35
        versionCode = 51
        versionName = "3.5.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Used for setup Bundle name.
        setProperty("archivesBaseName", "${applicationId}_${versionName}_${versionCode}_${SimpleDateFormat("dd.MM.yyyy").format(Date())}")
    }

    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE",
                "META-INF/NOTICE",
                "META-INF/MANIFEST.MF",
                "META-INF/rxjava.properties",
                "META-INF/proguard/okhttp3.pro",
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            )
        )
    }

    lint {
        disable.addAll(listOf(
            "RestrictedApi",
            "MissingTranslation"
        ))
    }

    compileOptions {
        // Flag to enable support for the new language APIs.
        isCoreLibraryDesugaringEnabled = true
        // Sets Java compatibility to Java 17.
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    signingConfigs {
        create("qa") {
            val keyProps = Properties()
            keyProps.load(FileInputStream(file("../local.properties")))
            storeFile = file(keyProps["debug.store.path"]!!)
            keyAlias = keyProps["debug.key.alias"].toString()
            storePassword = keyProps["debug.store.password"].toString()
            keyPassword = keyProps["debug.key.password"].toString()
        }
        create("vault") {
            val keyProps = Properties()
            keyProps.load(FileInputStream(file("../local.properties")))
            storeFile = file(keyProps["release.store.path"]!!)
            keyAlias = keyProps["release.key.alias"].toString()
            storePassword = keyProps["release.store.password"].toString()
            keyPassword = keyProps["release.key.password"].toString()
        }
    }

    flavorDimensions.add("mode")
    productFlavors {
        create("qa") {
            applicationId = "com.lobstr.stellar.vault.qa"
            resValue("string", "app_name", "QA Vault")
            resValue("string", "authority", "public.shared.data.preference.qa")
        }
        create("vault") {
            applicationId = "com.lobstr.stellar.vault"
            resValue("string", "app_name", "LOBSTR Vault")
            resValue("string", "authority", "public.shared.data.preference")
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("qa")
            isDebuggable = true
            aaptOptions.cruncherEnabled = false
            buildConfigField("boolean", "isDEBUG", "true")
        }

        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("vault")
            isDebuggable = false
            buildConfigField("boolean", "isDEBUG", "false")
        }

        applicationVariants.all {
            outputs.forEach { output ->
                if (output is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
                    val apkName = when (flavorName) {
                        "qa" -> {
                            when (buildType.name) {
                                "debug" -> "LOBSTR_Vault_qa_staging"
                                "release" -> "LOBSTR_Vault_qa_prod"
                                else -> applicationId
                            }
                        }
                        "vault" -> {
                            when (buildType.name) {
                                "debug" -> "LOBSTR_Vault_staging"
                                "release" -> "LOBSTR_Vault_market"
                                else -> applicationId
                            }
                        }
                        else -> applicationId
                    }

                    val date = SimpleDateFormat("dd.MM.yyyy").format(Date())

                    output.outputFileName =
                        "${apkName}_${versionName}_${versionCode}_${date}.${output.outputFile.extension}"
                }
            }
        }
    }

    configurations {
        all {
            exclude(group = "org.apache.httpcomponents", module = "httpclient")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    namespace = "com.lobstr.stellar.vault"
}

val desugar_jdk_libs by extra("2.1.5")
val rx_java by extra("3.1.10")
val rx_android by extra("3.0.2")
val retrofit by extra("3.0.0")
val okhttp_bom by extra("4.12.0")
val moxy by extra("2.2.2")
val glide by extra("4.16.0")
val material by extra("1.12.0")
val browser by extra("1.8.0")
val firebase_bom by extra("33.14.0")
val play_service_base by extra("18.7.0")
val androidx_core by extra("1.15.0")
val androidx_appcompat by extra("1.7.1")
val fragment by extra("1.8.8")
val recyclerview by extra("1.4.0")
val androidx_preference by extra("1.2.1")
val androidx_constraintlayout by extra("2.2.1")
val androidx_legacy_support_v4 by extra("1.0.0")
val junit by extra("4.13.2")
val runner by extra("1.2.1")
val espresso_core by extra("3.6.1")
val stellar_sdk by extra("1.5.0")
val mnemonic by extra("0.1.1")
val work_manager by extra("2.10.1")
val biometric by extra("1.1.0")
val lottieVersion by extra("6.6.6")
val qr_gen by extra("2.6.0")
val viewpager2 by extra("1.1.0")
val tangem by extra("3.7.2")
val hilt by extra("2.56.2")
val androidx_hilt by extra("1.2.0")
val lifecycle by extra("2.9.1")
val timber by extra("5.0.1")
val bcprov by extra("1.80")

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(mapOf("path" to ":tsmapper")))
    implementation(project(mapOf("path" to ":pinlockview")))
    testImplementation("junit:junit:$junit")
    androidTestImplementation("androidx.test.ext:junit:$runner")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espresso_core")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugar_jdk_libs")

    implementation("network.lightsail:stellar-sdk:$stellar_sdk")
    implementation("network.lightsail:stellar-sdk-android-spi:$stellar_sdk")
    implementation("network.lightsail:mnemonic4j:$mnemonic")

    // Android.
    implementation("com.google.android.gms:play-services-base:$play_service_base")
    implementation("androidx.core:core-ktx:$androidx_core")
    implementation("androidx.appcompat:appcompat:$androidx_appcompat")
    implementation("androidx.fragment:fragment-ktx:$fragment")
    implementation("androidx.recyclerview:recyclerview:$recyclerview")
    implementation("androidx.constraintlayout:constraintlayout:$androidx_constraintlayout")
    implementation("androidx.legacy:legacy-support-v4:$androidx_legacy_support_v4")
    implementation("androidx.preference:preference-ktx:$androidx_preference")
    implementation("com.google.android.material:material:$material")
    implementation("androidx.browser:browser:$browser")
    implementation("androidx.work:work-runtime-ktx:$work_manager")
    implementation("androidx.biometric:biometric:$biometric")
    implementation("androidx.viewpager2:viewpager2:$viewpager2")

    // Dagger Hilt dependencies.
    implementation("com.google.dagger:hilt-android:$hilt")
    ksp("com.google.dagger:hilt-compiler:$hilt")

    implementation("androidx.hilt:hilt-work:$androidx_hilt")
    ksp("androidx.hilt:hilt-compiler:$androidx_hilt")

    // Rx dependencies.
    implementation("io.reactivex.rxjava3:rxjava:$rx_java")
    implementation("io.reactivex.rxjava3:rxandroid:$rx_android")

    // Retrofit.
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:adapter-rxjava3:$retrofit")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:$okhttp_bom"))
    implementation("com.squareup.okhttp3:logging-interceptor")
    // Moxy.
    implementation("com.github.moxy-community:moxy:$moxy")
    implementation("com.github.moxy-community:moxy-ktx:$moxy")
    kapt("com.github.moxy-community:moxy-compiler:$moxy")

    // Glide.
    implementation("com.github.bumptech.glide:glide:$glide")
    implementation("com.github.bumptech.glide:okhttp3-integration:$glide")
    ksp("com.github.bumptech.glide:ksp:$glide")

    // Firebase.
    // Import the BoM for the Firebase platform.
    implementation(platform("com.google.firebase:firebase-bom:$firebase_bom"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    // Tangem.
    implementation ("com.github.tangem.tangem-sdk-android:android:$tangem")
    implementation ("com.github.tangem.tangem-sdk-android:core:$tangem")

    // Other.
    implementation("com.airbnb.android:lottie:$lottieVersion")
    implementation("com.github.kenglxn.QRGen:android:$qr_gen")
    implementation("com.jakewharton.timber:timber:$timber")

    // Lifecycles.
    // Lifecycles only (without ViewModel or LiveData)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
    // ProcessLifecycleOwner provides a lifecycle for the whole application process.
    implementation("androidx.lifecycle:lifecycle-process:$lifecycle")
    // Annotation processor (if using Java8, use the following instead of lifecycle-compiler).
    implementation("androidx.lifecycle:lifecycle-common:$lifecycle")

    implementation("org.bouncycastle:bcprov-jdk18on:$bcprov")
}