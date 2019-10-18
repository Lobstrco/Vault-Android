package com.lobstr.stellar.vault.presentation.util.biometric

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.TaskExecutors.MAIN_THREAD
import java.util.concurrent.Executor

/**
 * https://github.com/anitaa1990/Biometric-Auth-Sample
 */
open class BiometricManager protected constructor(biometricBuilder: BiometricBuilder) {

    companion object {
        val LOG_TAG = BiometricManager::class.simpleName
    }

    private var context: Context
    private var title: String?
    private var subtitle: String?
    private var description: String?
    private var negativeButtonText: String?

    private var biometricPrompt: BiometricPrompt?

    init {
        this.context = biometricBuilder.context
        this.title = biometricBuilder.title
        this.subtitle = biometricBuilder.subtitle
        this.description = biometricBuilder.description
        this.negativeButtonText = biometricBuilder.negativeButtonText

        val fragment = biometricBuilder.biometricListener as? Fragment
        val activity = biometricBuilder.biometricListener as? FragmentActivity

        biometricPrompt = when {
            fragment != null -> BiometricPrompt(
                fragment,
                biometricBuilder.executor,
                BiometricCallback(biometricBuilder.biometricListener)
            )
            activity != null -> BiometricPrompt(
                activity,
                biometricBuilder.executor,
                BiometricCallback(biometricBuilder.biometricListener)
            )
            else -> null
        }
    }

    fun cancelAuthentication() {
        biometricPrompt?.cancelAuthentication()
    }

    fun authenticate(biometricListener: BiometricListener) {
        if (title.isNullOrEmpty()) {
            Log.e(LOG_TAG, "Biometric Dialog title cannot be null")
            biometricListener.onBiometricAuthenticationInternalError("Biometric Dialog title cannot be null")
            return
        }

        if (negativeButtonText.isNullOrEmpty()) {
            Log.e(LOG_TAG, "Biometric Dialog negative button text cannot be null")
            biometricListener.onBiometricAuthenticationInternalError("Biometric Dialog negative button text cannot be null")
            return
        }

        if (!BiometricUtils.isPermissionGranted(context)) {
            biometricListener.onBiometricAuthenticationPermissionNotGranted()
            return
        }

        displayBiometricDialog()
    }

    private fun displayBiometricDialog() {
        if (BiometricUtils.isBiometricSupported(context)) {
            displayBiometricPrompt()
        }
    }

    private fun displayBiometricPrompt() {
        try {
            biometricPrompt?.authenticate(
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title!!)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setNegativeButtonText(negativeButtonText!!)
                    .build()
            )
        } catch (exc: Exception) {
            // handle internal errors
            exc.printStackTrace()
        }
    }

    class BiometricBuilder(val context: Context, val biometricListener: BiometricListener) {
        var title: String? = null
        var subtitle: String? = null
        var description: String? = null
        var negativeButtonText: String? = null
        var executor: Executor = MAIN_THREAD

        /**
         * required field
         */
        fun setTitle(title: String): BiometricBuilder {
            this.title = title
            return this
        }

        fun setSubtitle(subtitle: String?): BiometricBuilder {
            this.subtitle = subtitle
            return this
        }

        fun setDescription(description: String?): BiometricBuilder {
            this.description = description
            return this
        }

        /**
         * required field
         */
        fun setNegativeButtonText(negativeButtonText: String): BiometricBuilder {
            this.negativeButtonText = negativeButtonText
            return this
        }

        /**
         * Set executor for handle biometric callback if needed
         * By default - MAIN_THREAD
         */
        fun setExecutor(executor: Executor): BiometricBuilder {
            this.executor = executor
            return this
        }

        fun build(): BiometricManager {
            return BiometricManager(this)
        }
    }
}
