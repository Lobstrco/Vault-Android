package com.lobstr.stellar.vault.data.vault_auth

import com.lobstr.stellar.vault.data.net.VaultAuthApi
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import io.reactivex.Single

class VaultAuthRepositoryImpl(
    private val vaultAuthApi: VaultAuthApi,
    private val rxErrorUtils: RxErrorUtils
) : VaultAuthRepository {

    override fun getChallenge(account: String): Single<String> {
        return vaultAuthApi.getChallenge(account)
            .onErrorResumeNext {
                rxErrorUtils.handleSingleRequestHttpError(it)
            }
            .map {
                it.transaction!!
            }
    }

    override fun submitChallenge(transaction: String): Single<String> {
        return vaultAuthApi.submitChallenge(transaction)
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
            .map {
                it.token!!
            }
    }
}