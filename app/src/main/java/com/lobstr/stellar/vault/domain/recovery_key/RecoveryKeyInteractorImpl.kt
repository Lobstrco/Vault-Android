package com.lobstr.stellar.vault.domain.recovery_key

import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single
import org.stellar.sdk.KeyPair

class RecoveryKeyInteractorImpl(
    private val stellarRepository: StellarRepository,
    private val prefUtil: PrefsUtil
) : RecoveryKeyInteractor {

    override fun createSecretKey(mnemonics: CharArray): Single<String> {
        return stellarRepository.createKeyPair(mnemonics, 0)
            .map { keyPair: KeyPair ->
                prefUtil.publicKey = keyPair.accountId
                return@map String(keyPair.secretSeed)
            }
    }
}