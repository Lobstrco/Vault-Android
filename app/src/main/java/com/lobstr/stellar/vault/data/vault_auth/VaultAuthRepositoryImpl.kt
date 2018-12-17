package com.lobstr.stellar.vault.data.vault_auth

import com.lobstr.stellar.vault.data.net.VaultAuthApi
import com.lobstr.stellar.vault.data.net.entities.vault_auth.ApiGetChallenge
import com.lobstr.stellar.vault.data.net.entities.vault_auth.ApiSubmitChallenge
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import io.reactivex.Single


class VaultAuthRepositoryImpl(private val vaultAuthApi: VaultAuthApi) : VaultAuthRepository {

    override fun getChallenge(account: String): Single<String> {
        return vaultAuthApi.getChallenge(account)
            .map { response ->
                val apiGetChallengeResponse: ApiGetChallenge = response.body()!!
                apiGetChallengeResponse.transaction!!
            }
    }

    override fun submitChallenge(transaction: String): Single<String> {
        return vaultAuthApi.submitChallenge(transaction)
            .map { response ->
                val apiSubmitChallengeResponse: ApiSubmitChallenge = response.body()!!
                apiSubmitChallengeResponse.token!!
            }
    }
}