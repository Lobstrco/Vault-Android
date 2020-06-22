package com.lobstr.stellar.vault.domain.tangem.setup


interface TangemSetupInteractor {

    fun savePublicKey(publicKey: String)

    fun saveTangemCardId(cardId: String)
}