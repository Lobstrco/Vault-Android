package com.lobstr.stellar.vault.domain.mnemonics

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single


class MnemonicsInteractorImpl(private val keyStoreRepository: KeyStoreRepository) : MnemonicsInteractor {

    override fun getPhrases(): Single<String> {
        return Single.fromCallable {
            return@fromCallable keyStoreRepository.decryptData(
                PrefsUtil.PREF_ENCRYPTED_PHRASES,
                PrefsUtil.PREF_PHRASES_IV
            )
        }
    }
}