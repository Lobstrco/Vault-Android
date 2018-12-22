package com.lobstr.stellar.vault.domain.recovery_key

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single
import org.stellar.sdk.KeyPair

class RecoveryKeyInteractorImpl(
    private val stellarRepository: StellarRepository,
    private val keyStoreRepository: KeyStoreRepository,
    private val prefUtil: PrefsUtil
) : RecoveryKeyInteractor {

    override fun createAndSaveSecretKey(mnemonics: CharArray): Single<String> {
        return stellarRepository.createKeyPair(mnemonics, 0)
            .map { keyPair: KeyPair ->
                keyStoreRepository.encryptData(
                    String(mnemonics),
                    PrefsUtil.PREF_ENCRYPTED_PHRASES,
                    PrefsUtil.PREF_PHRASES_IV
                )
                prefUtil.publicKey = keyPair.accountId
                return@map String(keyPair.secretSeed)
            }
    }
}