package com.lobstr.stellar.vault.domain.account_name

import com.lobstr.stellar.vault.domain.account.AccountRepository


class AccountNameInteractorImpl(private val accountRepository: AccountRepository) : AccountNameInteractor {

    override fun getAccountName(publicKey: String): String? {
        return accountRepository.getAccountNames()[publicKey]
    }

    override fun saveAccountName(publicKey: String, name: String?) {
        accountRepository.saveAccountName(publicKey, name)
    }
}