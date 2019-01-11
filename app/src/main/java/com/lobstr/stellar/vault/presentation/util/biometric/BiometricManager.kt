package com.lobstr.stellar.vault.presentation.util.biometric

import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal

/**
 * https://github.com/anitaa1990/Biometric-Auth-Sample
 */
open class BiometricManager protected constructor(biometricBuilder: BiometricBuilder) : BiometricManagerV23() {

    init {
        this.context = biometricBuilder.context
        this.title = biometricBuilder.title
        this.subtitle = biometricBuilder.subtitle
        this.description = biometricBuilder.description
        this.negativeButtonText = biometricBuilder.negativeButtonText
    }

    fun isDialogShowing(): Boolean {
        return biometricDialogV23?.isShowing ?: false
    }

    fun authenticate(biometricCallback: BiometricCallback) {
        if (title == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog title cannot be null")
            return
        }

        if (subtitle == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog subtitle cannot be null")
        }

        if (description == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog description cannot be null")
            return
        }

        if (negativeButtonText == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog negative button text cannot be null")
            return
        }

        if (!BiometricUtils.isSdkVersionSupported) {
            biometricCallback.onSdkVersionNotSupported()
            return
        }

        if (!BiometricUtils.isPermissionGranted(context!!)) {
            biometricCallback.onBiometricAuthenticationPermissionNotGranted()
            return
        }

        if (!BiometricUtils.isHardwareSupported(context!!)) {
            biometricCallback.onBiometricAuthenticationNotSupported()
            return
        }

        if (!BiometricUtils.isFingerprintAvailable(context!!)) {
            biometricCallback.onBiometricAuthenticationNotAvailable()
            return
        }

        displayBiometricDialog(biometricCallback)
    }

    private fun displayBiometricDialog(biometricCallback: BiometricCallback) {
        if (BiometricUtils.isBiometricPromptEnabled) {
            displayBiometricPrompt(biometricCallback)
        } else {
            displayBiometricPromptV23(biometricCallback)
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private fun displayBiometricPrompt(biometricCallback: BiometricCallback) {
        BiometricPrompt.Builder(context)
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButton(
                negativeButtonText,
                context!!.mainExecutor,
                DialogInterface.OnClickListener { _, _ -> biometricCallback.onAuthenticationCancelled() })
            .build()
            .authenticate(
                CancellationSignal(), context!!.mainExecutor,
                BiometricCallbackV28(biometricCallback)
            )
    }

    class BiometricBuilder(val context: Context) {
        var title: String? = null
        var subtitle: String? = null
        var description: String? = null
        var negativeButtonText: String? = null

        fun setTitle(title: String): BiometricBuilder {
            this.title = title
            return this
        }

        fun setSubtitle(subtitle: String): BiometricBuilder {
            this.subtitle = subtitle
            return this
        }

        fun setDescription(description: String): BiometricBuilder {
            this.description = description
            return this
        }


        fun setNegativeButtonText(negativeButtonText: String): BiometricBuilder {
            this.negativeButtonText = negativeButtonText
            return this
        }

        fun build(): BiometricManager {
            return BiometricManager(this)
        }
    }
}
