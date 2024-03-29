package com.lobstr.stellar.vault.domain.base

import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single

class BaseInteractorImpl(
    private val vaultAuthRepository: VaultAuthRepository,
    private val localDataRepository: LocalDataRepository,
    private val prefsUtil: PrefsUtil
) : BaseInteractor {

    override fun getTangemCardId(): String? {
        return prefsUtil.tangemCardId
    }

    override fun hasAuthToken(): Boolean {
        return !prefsUtil.authToken.isNullOrEmpty()
    }

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }

    override fun getChallenge(): Single<String> {
        return vaultAuthRepository.getChallenge(getUserPublicKey())
    }

    override fun authorizeVault(transaction: String): Single<String> {
        return vaultAuthRepository.submitChallenge(transaction)
            .doOnSuccess {
                prefsUtil.authToken = it
                localDataRepository.saveAuthToken(prefsUtil.publicKey!!, it)
            }
    }

    override fun hasEncryptedPin(): Boolean = !prefsUtil.encryptedPin.isNullOrEmpty()

    override fun changePublicKeyInfo(publicKey: String) {
        prefsUtil.publicKey = publicKey
        prefsUtil.authToken = localDataRepository.getAuthToken(publicKey)
    }
}