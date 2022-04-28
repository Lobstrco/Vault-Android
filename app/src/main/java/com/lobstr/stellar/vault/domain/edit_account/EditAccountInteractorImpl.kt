package com.lobstr.stellar.vault.domain.edit_account

import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil

class EditAccountInteractorImpl(
    private val localDataRepository: LocalDataRepository,
    private val prefsUtil: PrefsUtil
) : EditAccountInteractor {

    override fun getPublicKeyList(): List<Pair<String, Int>> = prefsUtil.getPublicKeyDataList()

    override fun getAccountName(publicKey: String): String? =
        localDataRepository.getAccountNames()[publicKey]

    override fun clearAccountName(publicKey: String) {
        localDataRepository.saveAccountName(publicKey, null)
    }
}