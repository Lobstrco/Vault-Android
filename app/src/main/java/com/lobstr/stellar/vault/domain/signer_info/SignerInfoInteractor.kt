package com.lobstr.stellar.vault.domain.signer_info


interface SignerInfoInteractor {
    fun getUserPublicKey(): String?
}