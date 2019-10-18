package com.lobstr.stellar.vault.domain.biometric

import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.DISABLED
import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.ENABLED
import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class BiometricSetUpInteractorImpl(private val prefsUtil: PrefsUtil) : BiometricSetUpInteractor {
    override fun setBiometricEnabled(enabled: Boolean) {
        prefsUtil.biometricState = if (enabled) ENABLED else DISABLED
    }
}