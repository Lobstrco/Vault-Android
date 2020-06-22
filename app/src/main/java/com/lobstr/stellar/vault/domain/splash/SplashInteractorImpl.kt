package com.lobstr.stellar.vault.domain.splash

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class SplashInteractorImpl(
    private val prefsUtil: PrefsUtil,
    private val keyStoreRepository: KeyStoreRepository
) : SplashInteractor {

    override fun hasMnemonics(): Boolean {
        return !prefsUtil.encryptedPhrases.isNullOrEmpty()
    }

    override fun hasEncryptedPin(): Boolean {
        return !prefsUtil.encryptedPin.isNullOrEmpty()
    }

    override fun hasPublicKey(): Boolean {
        return !prefsUtil.publicKey.isNullOrEmpty()
    }

    override fun hasAuthToken(): Boolean {
        return !prefsUtil.authToken.isNullOrEmpty()
    }

    override fun hasSigners(): Boolean {
        return prefsUtil.accountHasSigners
    }

    override fun clearUserData() {
        prefsUtil.clearUserPrefs()
        keyStoreRepository.clearAll()
    }
}