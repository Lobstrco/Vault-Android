package com.lobstr.stellar.vault.domain.tangem.setup

import com.lobstr.stellar.vault.presentation.util.PrefsUtil

class TangemSetupInteractorImpl(private val prefsUtil: PrefsUtil) :
    TangemSetupInteractor {

    override fun savePublicKey(publicKey: String) {
        prefsUtil.publicKey = publicKey
        prefsUtil.savePublicKeyToList(publicKey, 0)
    }

    override fun saveTangemCardId(cardId: String) {
        prefsUtil.tangemCardId = cardId
    }
}