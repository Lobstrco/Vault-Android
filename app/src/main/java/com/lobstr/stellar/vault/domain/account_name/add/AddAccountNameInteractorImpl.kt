package com.lobstr.stellar.vault.domain.account_name.add

import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository

class AddAccountNameInteractorImpl(
    private val localDataRepository: LocalDataRepository
) : AddAccountNameInteractor {

    override fun getAccountNames(): Map<String, String?> {
        return localDataRepository.getAccountNames()
    }

    override fun saveAccountName(publicKey: String, name: String?) {
        localDataRepository.saveAccountName(publicKey, name)
    }
}