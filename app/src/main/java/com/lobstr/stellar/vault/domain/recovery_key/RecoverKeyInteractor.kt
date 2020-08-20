package com.lobstr.stellar.vault.domain.recovery_key

import io.reactivex.rxjava3.core.Single

interface RecoverKeyInteractor {
    fun createAndSaveSecretKey(mnemonics: CharArray): Single<String>
}