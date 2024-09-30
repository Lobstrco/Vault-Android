package com.lobstr.stellar.vault.domain.confirm_mnemonics

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single
import org.stellar.sdk.KeyPair

class ConfirmMnemonicsInteractorImpl(
    private val stellarRepository: StellarRepository,
    private val keyStoreRepository: KeyStoreRepository,
    private val prefUtil: PrefsUtil
) : ConfirmMnemonicsInteractor {

    override fun createAndSaveSecretKey(mnemonics: String): Single<String> {
        val newKeyIndex: Int = prefUtil.getNewPublicKeyIndex()
        return stellarRepository.createKeyPair(mnemonics, newKeyIndex)
            .map { keyPair: KeyPair ->
                keyStoreRepository.encryptData(
                    mnemonics,
                    PrefsUtil.PREF_ENCRYPTED_PHRASES,
                    PrefsUtil.PREF_PHRASES_IV
                )
                prefUtil.savePublicKeyToList(keyPair.accountId, newKeyIndex)
                prefUtil.publicKey = keyPair.accountId
                return@map String(keyPair.secretSeed)
            }
    }
}