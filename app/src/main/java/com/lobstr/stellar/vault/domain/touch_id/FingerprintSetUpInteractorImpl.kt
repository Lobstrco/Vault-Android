package com.lobstr.stellar.vault.domain.touch_id

import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.DISABLED
import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.ENABLED
import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class FingerprintSetUpInteractorImpl(private val prefsUtil: PrefsUtil) : FingerprintSetUpInteractor {
    override fun setTouchIdEnabled(enabled: Boolean) {
        prefsUtil.biometricState = if (enabled) ENABLED else DISABLED
    }
}