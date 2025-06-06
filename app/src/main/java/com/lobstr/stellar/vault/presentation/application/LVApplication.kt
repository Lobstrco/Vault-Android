package com.lobstr.stellar.vault.presentation.application

import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.BuildType.DEBUG
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import org.bouncycastle.jce.provider.BouncyCastleProvider
import timber.log.Timber
import java.security.Provider
import java.security.Security
import javax.inject.Inject

@HiltAndroidApp
class LVApplication : Application(), Configuration.Provider {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    companion object {
        lateinit var appContext: Context

        // App Pin appearance flag handled in AppLifecycleListener.
        var checkPinAppearance = true

        // Rate Us dialog flag.
        var checkRateUsDialogState: Byte = Constant.RateUsSessionState.UNDEFINED
            set(value) {
                if (field != Constant.RateUsSessionState.CHECKED) {
                    field = value
                }
            }
    }

    private val lifecycleListener: AppLifecycleListener by lazy {
        AppLifecycleListener()
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
        appContext = applicationContext

        if (BuildConfig.BUILD_TYPE == DEBUG) Timber.plant(Timber.DebugTree())
        enableStrictMode()
        Firebase.analytics.setAnalyticsCollectionEnabled(BuildConfig.BUILD_TYPE != DEBUG)
        setupLifecycleListener()
        setupRxJavaErrorHandler()
    }

    // NOTE Used for Inject data in Worker.
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    // ===========================================================
    // Methods
    // ===========================================================

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

    private fun setupLifecycleListener() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleListener)
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