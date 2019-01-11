package com.lobstr.stellar.vault.domain.touch_id

import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class FingerprintSetUpInteractorImpl(private val prefsUtil: PrefsUtil) : FingerprintSetUpInteractor {
    override fun setTouchIdEnabled(enabled: Boolean) {
        prefsUtil.isTouchIdEnabled = enabled
    }
}