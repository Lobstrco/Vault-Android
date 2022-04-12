package com.lobstr.stellar.vault.domain.accounts

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single
import org.stellar.sdk.KeyPair

class AccountsInteractorImpl(
    private val prefsUtil: PrefsUtil,
    private val stellarRepository: StellarRepository,
    private val vaultAuthRepository: VaultAuthRepository,
    private val keyStoreRepository: KeyStoreRepository,
    private val localDataRepository: LocalDataRepository
) : AccountsInteractor {

    override fun getPublicKeyList(): List<Pair<String, Int>> = prefsUtil.getPublicKeyDataList()

    override fun getCurrentPublicKey(): String = prefsUtil.publicKey ?: ""

    override fun saveNewKeyData(key: String) {
        prefsUtil.publicKey = key
        prefsUtil.authToken = localDataRepository.getAuthToken(key)
    }

    override fun generateNewKeyPair(): Single<String> {
        var newKeyPair: KeyPair? = null
        val newKeyIndex: Int = prefsUtil.getNewPublicKeyIndex()
        return getPhrases()
            .flatMap {
                stellarRepository.createKeyPair(
                    it.toCharArray(),
                    newKeyIndex
                )
            }
            .flatMap {
                newKeyPair = it
                vaultAuthRepository.getChallenge(newKeyPair!!.accountId)
            }
            .flatMap { stellarRepository.signTransaction(newKeyPair!!, it) }
            .flatMap { vaultAuthRepository.submitChallenge(it.toEnvelopeXdrBase64()) }
            .doOnSuccess {
                prefsUtil.savePublicKeyToList(newKeyPair!!.accountId, newKeyIndex)
                prefsUtil.publicKey = newKeyPair!!.accountId
                prefsUtil.authToken = it
                localDataRepository.saveAuthToken(newKeyPair!!.accountId, it)
            }
    }

    private fun getPhrases(): Single<String> = Single.fromCallable {
        keyStoreRepository.decryptData(
            PrefsUtil.PREF_ENCRYPTED_PHRASES,
            PrefsUtil.PREF_PHRASES_IV
        )
    }

    override fun getAccountNames(): Map<String, String?> = localDataRepository.getAccountNames()
}