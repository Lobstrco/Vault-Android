package com.lobstr.stellar.vault.presentation.application

import android.content.Intent
import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.analytics.FirebaseAnalytics
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.dagger.component.AppComponent
import com.lobstr.stellar.vault.presentation.dagger.component.DaggerAppComponent
import com.lobstr.stellar.vault.presentation.dagger.module.AppModule
import com.lobstr.stellar.vault.presentation.util.Constant.BuildType.DEBUG
import com.zendesk.logger.Logger
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import org.bouncycastle.jce.provider.BouncyCastleProvider
import zendesk.core.Zendesk
import zendesk.support.Support
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
        lateinit var appComponent: AppComponent
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    init {
        setupBouncyCastle()
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    override fun onCreate() {
        super.onCreate()
        upgradeSecurityProvider()
        enableStrictMode()
        FirebaseAnalytics.getInstance(this)
            .setAnalyticsCollectionEnabled(BuildConfig.BUILD_TYPE != DEBUG)
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        configureZendesk()

        setupRxJavaErrorHandler()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * Fix for SSLException.
     * Enable SSL compatibility in pre-lollipop and lollipop devices.
     */
    private fun upgradeSecurityProvider() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                ProviderInstaller.installIfNeededAsync(
                    this,
                    object : ProviderInstaller.ProviderInstallListener {
                        override fun onProviderInstallFailed(
                            errorCode: Int,
                            recoveryIntent: Intent?
                        ) {
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

    /**
     * For create Mnemonics.
     * Setup Bouncy Castle as well, because some of the security algorithms used within
     * the library are supported starting with Java 1.8 only.
     */
    private fun setupBouncyCastle() {
        // Remove default android BC provider.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        // Add new BC provider.
        Security.addProvider(BouncyCastleProvider() as Provider?)
    }

    private fun enableStrictMode() {
        if (BuildConfig.BUILD_TYPE == DEBUG) {
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

    private fun configureZendesk() {
        Zendesk.INSTANCE.init(
            this,
            resources.getString(R.string.zd_url),
            resources.getString(R.string.zd_appid),
            resources.getString(R.string.zd_oauth)
        )
        Support.INSTANCE.init(Zendesk.INSTANCE)
        Logger.setLoggable(BuildConfig.BUILD_TYPE == DEBUG)
    }

    /**
     * Used for avoid UndeliverableException.
     */
    private fun setupRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler { throwable: Throwable? ->
            if (throwable is UndeliverableException) {
                throwable.printStackTrace()
            }
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}