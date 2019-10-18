package com.lobstr.stellar.vault.presentation.util.biometric

import androidx.biometric.BiometricPrompt

class BiometricCallback(private val biometricListener: BiometricListener) :
    BiometricPrompt.AuthenticationCallback() {

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)

        biometricListener.onAuthenticationError(errorCode, errString)
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)

        biometricListener.onAuthenticationSuccessful()
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()

        biometricListener.onAuthenticationFailed()
    }
}
