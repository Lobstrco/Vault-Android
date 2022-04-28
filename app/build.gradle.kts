import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "com.lobstr.stellar.vault"
        minSdk = 22
        targetSdk = 32
        versionCode = 33
        versionName = "3.0.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Used for setup Bundle name.
        setProperty("archivesBaseName", "${applicationId}_${versionName}(${versionCode})_${SimpleDateFormat("dd.MM.yyyy").format(Date())})")
    }

    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE",
                "META-INF/NOTICE",
                "META-INF/MANIFEST.MF",
                "META-INF/rxjava.properties",
                "META-INF/proguard/okhttp3.pro"
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
        // Sets Java compatibility to Java 8.
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
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
                        "${apkName}_${versionName}(${versionCode})_${date}.${output.outputFile.extension}"
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
    }
}

val desugar_jdk_libs by extra("1.1.5")
val dagger by extra("2.28.1")
val rx_java by extra("3.1.4")
val rx_android by extra("3.0.0")
val rx_kotlin by extra("3.0.1")
val retrofit by extra("2.9.0")
val okhttp by extra("4.9.0")
val moxy by extra("2.2.2")
val glide by extra("4.13.1")
val material by extra("1.5.0")
val browser by extra("1.4.0")
val firebase_bom by extra("29.3.1")
val javax_annotation by extra("10.0-b28")
val play_service_base by extra("18.0.1")
val androidx_core by extra("1.7.0")
val androidx_appcompat by extra("1.4.1")
val fragment by extra("1.4.1")
val recyclerview by extra("1.2.1")
val androidx_preference by extra("1.2.0")
val androidx_constraintlayout by extra("2.1.3")
val androidx_legacy_support_v4 by extra("1.0.0")
val junit by extra("4.13.2")
val runner by extra("1.1.3")
val espresso_core by extra("3.4.0")
val stellar_sdk by extra("0.32.0")
val work_manager by extra("2.7.1")
val biometric by extra("1.1.0")
val lottieVersion by extra("5.0.3")
val qr_gen by extra("2.6.0")
val viewpager2 by extra("1.0.0")
val tangem by extra("0.9.0")
val zendesk by extra("5.0.8")
val hilt by extra("2.41")
val androidx_hilt by extra("1.0.0")
val lifecycle by extra("2.4.1")

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(mapOf("path" to ":tsmapper")))
    implementation(project(mapOf("path" to ":pinlockview")))
    testImplementation("junit:junit:$junit")
    androidTestImplementation("androidx.test.ext:junit:$runner")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espresso_core")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugar_jdk_libs")

    // Stellar.
    implementation("com.github.stellar:java-stellar-sdk:$stellar_sdk")

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
    kapt("com.google.dagger:hilt-compiler:$hilt")

    implementation("androidx.hilt:hilt-work:$androidx_hilt")
    kapt("androidx.hilt:hilt-compiler:$androidx_hilt")

    // Rx dependencies.
    implementation("io.reactivex.rxjava3:rxjava:$rx_java")
    implementation("io.reactivex.rxjava3:rxandroid:$rx_android")
    implementation("io.reactivex.rxjava3:rxkotlin:$rx_kotlin")

    // Retrofit.
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:adapter-rxjava3:$retrofit")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp")

    // Moxy.
    implementation("com.github.moxy-community:moxy:$moxy")
    implementation("com.github.moxy-community:moxy-ktx:$moxy")
    kapt("com.github.moxy-community:moxy-compiler:$moxy")

    // Glide.
    implementation("com.github.bumptech.glide:glide:$glide")
    implementation("com.github.bumptech.glide:okhttp3-integration:$glide")
    kapt("com.github.bumptech.glide:compiler:$glide")

    // Firebase.
    // Import the BoM for the Firebase platform.
    implementation(platform("com.google.firebase:firebase-bom:$firebase_bom"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Tangem.
    implementation("com.github.tangem.tangem-sdk-android:tangem-core:$tangem")
    implementation("com.github.tangem.tangem-sdk-android:tangem-sdk:$tangem")

    // Other.
    implementation("com.airbnb.android:lottie:$lottieVersion")
    implementation("com.github.kenglxn.QRGen:android:$qr_gen")
    implementation("com.zendesk:support:$zendesk")

    // Lifecycles.
    // Lifecycles only (without ViewModel or LiveData)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
    // ProcessLifecycleOwner provides a lifecycle for the whole application process.
    implementation("androidx.lifecycle:lifecycle-process:$lifecycle")
    // Annotation processor (if using Java8, use the following instead of lifecycle-compiler).
    implementation("androidx.lifecycle:lifecycle-common:$lifecycle")
}
// NOTE Used for Dagger Hilt.
kapt {
    correctErrorTypes = true
}