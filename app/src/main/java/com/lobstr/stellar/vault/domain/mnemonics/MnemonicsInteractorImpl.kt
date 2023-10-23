package com.lobstr.stellar.vault.domain.mnemonics

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single


class MnemonicsInteractorImpl(
    private val keyStoreRepository: KeyStoreRepository,
    private val stellarRepository: StellarRepository
) : MnemonicsInteractor {

    override fun generate12WordMnemonics(): ArrayList<MnemonicItem> {
        return stellarRepository.generate12WordMnemonic()
    }

    override fun getExistingMnemonics(): Single<ArrayList<MnemonicItem>> {
        return Single.fromCallable {
            return@fromCallable keyStoreRepository.decryptDataToMnemonicItems(
                PrefsUtil.PREF_ENCRYPTED_PHRASES,
                PrefsUtil.PREF_PHRASES_IV
            )
        }
    }

    override fun getPhrases(): Single<String> {
        return Single.fromCallable {
            return@fromCallable keyStoreRepository.decryptData(
                PrefsUtil.PREF_ENCRYPTED_PHRASES,
                PrefsUtil.PREF_PHRASES_IV
            )!!
        }
    }
}