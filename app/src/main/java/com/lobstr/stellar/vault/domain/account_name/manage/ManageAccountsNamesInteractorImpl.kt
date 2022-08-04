package com.lobstr.stellar.vault.domain.account_name.manage

import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository

class ManageAccountsNamesInteractorImpl(
    private val localDataRepository: LocalDataRepository
) : ManageAccountsNamesInteractor {
    override fun getAccountNames(): Map<String, String?> {
        return localDataRepository.getAccountNames()
    }
}