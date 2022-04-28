package com.lobstr.stellar.vault.domain.show_public_key

import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil

class ShowPublicKeyInteractorImpl(
    private val localDataRepository: LocalDataRepository,
    private val prefsUtil: PrefsUtil
) : ShowPublicKeyInteractor {

    override fun getUserPublicKeyIndex(key: String): Int {
        return prefsUtil.getPublicKeyIndex(key)
    }
    override fun getAccountNames(): Map<String, String?> {
        return localDataRepository.getAccountNames()
    }

    override fun hasTangem(): Boolean {
        return !prefsUtil.tangemCardId.isNullOrEmpty()
    }
}