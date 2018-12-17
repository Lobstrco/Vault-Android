package com.lobstr.stellar.vault.domain.confirm_mnemonics

import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single
import org.stellar.sdk.KeyPair


class ConfirmMnemonicsInteractorImpl(
    private val stellarRepository: StellarRepository,
    private val prefUtil: PrefsUtil
) : ConfirmMnemonicsInteractor {

    override fun createSecretKey(mnemonics: CharArray): Single<String> {
        return stellarRepository.createKeyPair(mnemonics, 0)
            .map { keyPair: KeyPair ->
                prefUtil.publicKey = keyPair.accountId
                return@map String(keyPair.secretSeed)
            }
    }
}