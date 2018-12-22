package com.lobstr.stellar.vault.domain.settings

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class SettingsInteractorImpl(private val prefsUtil: PrefsUtil, private val keyStoreRepository: KeyStoreRepository) :
    SettingsInteractor {

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }

    override fun getSignedAccount(): String? {
        return prefsUtil.signedAccount
    }

    override fun clearUserData() {
        prefsUtil.clearUserPrefs()
        keyStoreRepository.clear()
    }
}