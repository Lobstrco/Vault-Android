package com.lobstr.stellar.vault.domain.account_name

import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository

class AccountNameInteractorImpl(
    private val localDataRepository: LocalDataRepository
) : AccountNameInteractor {

    override fun getAccountName(publicKey: String): String? =
        localDataRepository.getAccountNames()[publicKey]

    override fun saveAccountName(publicKey: String, name: String?) {
        localDataRepository.saveAccountName(publicKey, name)
    }
}