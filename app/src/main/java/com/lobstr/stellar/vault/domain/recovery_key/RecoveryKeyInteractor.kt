package com.lobstr.stellar.vault.domain.recovery_key

import io.reactivex.Single

interface RecoveryKeyInteractor {
    fun createSecretKey(mnemonics: CharArray): Single<String>
}