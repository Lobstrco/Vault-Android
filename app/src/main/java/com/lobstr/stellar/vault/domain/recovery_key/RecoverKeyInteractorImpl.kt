package com.lobstr.stellar.vault.domain.recovery_key

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single
import org.stellar.sdk.KeyPair

class RecoverKeyInteractorImpl(
    private val stellarRepository: StellarRepository,
    private val keyStoreRepository: KeyStoreRepository,
    private val prefUtil: PrefsUtil
) : RecoverKeyInteractor {

    override fun createAndSaveSecretKey(mnemonics: CharArray): Single<String> =
        stellarRepository.createKeyPair(mnemonics, 0)
            .map { keyPair: KeyPair ->
                keyStoreRepository.encryptData(
                    String(mnemonics),
                    PrefsUtil.PREF_ENCRYPTED_PHRASES,
                    PrefsUtil.PREF_PHRASES_IV
                )
                prefUtil.savePublicKeyToList(keyPair.accountId, 0)
                prefUtil.publicKey = keyPair.accountId
                return@map String(keyPair.secretSeed)
            }

    override fun createAdditionalPublicKey(mnemonics: CharArray, position: Int): Single<String> =
        stellarRepository.createKeyPair(mnemonics, position).map { it.accountId }

    override fun checkAccount(publicKey: String): Single<List<Account>> =
        stellarRepository.getAccountsForSigner(publicKey)

    override fun savePublicKeyToList(key: String, index: Int) {
        prefUtil.savePublicKeyToList(key, index)
    }
}