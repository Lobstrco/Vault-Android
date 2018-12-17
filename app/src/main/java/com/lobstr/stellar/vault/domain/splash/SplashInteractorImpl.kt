package com.lobstr.stellar.vault.domain.splash

import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class SplashInteractorImpl(private val prefsUtil: PrefsUtil) : SplashInteractor {

    override fun isUserAuthorized(): Boolean {
        return !prefsUtil.encryptedKey.isNullOrEmpty() && !prefsUtil.publicKey.isNullOrEmpty()
    }
}