package com.lobstr.stellar.vault.domain.transaction_success

import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class TransactionSuccessInteractorImpl(private val prefsUtil: PrefsUtil) :
    TransactionSuccessInteractor {
    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }
}