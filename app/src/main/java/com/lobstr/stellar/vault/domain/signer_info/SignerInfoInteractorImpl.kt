package com.lobstr.stellar.vault.domain.signer_info

import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class SignerInfoInteractorImpl(
    private val prefsUtil: PrefsUtil
) : SignerInfoInteractor {
    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }
}