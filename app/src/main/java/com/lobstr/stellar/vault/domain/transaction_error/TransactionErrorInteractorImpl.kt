package com.lobstr.stellar.vault.domain.transaction_error

import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class TransactionErrorInteractorImpl(private val prefsUtil: PrefsUtil) :
    TransactionErrorInteractor {
    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }
}