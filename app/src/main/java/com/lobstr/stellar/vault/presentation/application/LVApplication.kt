package com.lobstr.stellar.vault.presentation.application

import android.content.Intent
import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.analytics.FirebaseAnalytics
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.presentation.dagger.component.AppComponent
import com.lobstr.stellar.vault.presentation.dagger.component.DaggerAppComponent
import com.lobstr.stellar.vault.presentation.dagger.module.AppModule
import com.lobstr.stellar.vault.presentation.util.Constant
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Provider
import java.security.Security


class LVApplication : MultiDexApplication() {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    companion object {
        val LOG_TAG = LVApplication::class.simpleName
        lateinit var sAppComponent: AppComponent
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    override fun onCreate() {
        super.onCreate()

        upgradeSecurityProvider()
        setupBouncyCastle()
        enableStrictMode()
        FirebaseAnalytics.getInstance(this)
            .setAnalyticsCollectionEnabled(BuildConfig.BUILD_TYPE != Constant.BuildType.DEBUG)
        sAppComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * For create Mnemonics
     * Setup Bouncy Castle as well, because some of the security algorithms used within
     * the library are supported starting with Java 1.8 only.
     */
    private fun setupBouncyCastle() {
        Security.removeProvider("BC")
        Security.addProvider(BouncyCastleProvider() as Provider?)
    }

    private fun enableStrictMode() {
        if (BuildConfig.BUILD_TYPE == Constant.BuildType.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }

    /**
     * Fix for SSLException
     * Enable SSL compatibility in pre-lollipop and lollipop devices
     */
    private fun upgradeSecurityProvider() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                ProviderInstaller.installIfNeededAsync(this, object : ProviderInstaller.ProviderInstallListener {
                    override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent?) {
                        Log.e(LOG_TAG, "New security provider install failed.")
                    }

                    override fun onProviderInstalled() {
                        Log.e(LOG_TAG, "New security provider installed.")
                    }
                })
            } catch (ex: Exception) {
                Log.e(LOG_TAG, "Unknown issue trying to install a new security provider", ex)
            }
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}