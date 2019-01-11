package com.lobstr.stellar.vault.domain.settings

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class SettingsInteractorImpl(private val prefsUtil: PrefsUtil, private val keyStoreRepository: KeyStoreRepository) :
    SettingsInteractor {

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }

    override fun getSignersCount(): Int {
        return prefsUtil.accountSignersCount
    }

    override fun clearUserData() {
        prefsUtil.clearUserPrefs()
        keyStoreRepository.clearAll()
    }

    override fun isTouchIdEnabled(): Boolean {
        return prefsUtil.isTouchIdEnabled
    }

    override fun setTouchIdEnabled(enabled: Boolean) {
        prefsUtil.isTouchIdEnabled = enabled
    }
}