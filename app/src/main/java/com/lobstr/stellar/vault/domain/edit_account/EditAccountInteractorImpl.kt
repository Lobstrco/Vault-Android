package com.lobstr.stellar.vault.domain.edit_account

import com.lobstr.stellar.vault.domain.account.AccountRepository

class EditAccountInteractorImpl(private val accountRepository: AccountRepository) :
    EditAccountInteractor {

    override fun getAccountName(publicKey: String): String? {
        return accountRepository.getAccountNames()[publicKey]
    }

    override fun clearAccountName(publicKey: String) {
        accountRepository.saveAccountName(publicKey, null)
    }
}