package com.lobstr.stellar.vault.domain.recovery_key

import io.reactivex.Single

interface RecoveryKeyInteractor {
    fun createAndSaveSecretKey(mnemonics: CharArray): Single<String>
}