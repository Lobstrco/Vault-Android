package com.lobstr.stellar.vault.presentation.util.biometric

interface BiometricListener {

    fun onBiometricAuthenticationPermissionNotGranted()

    fun onBiometricAuthenticationInternalError(error: String?)

    fun onAuthenticationFailed()

    fun onAuthenticationSuccessful()

    fun onAuthenticationError(errorCode: Int, errString: CharSequence?)
}
