package com.lobstr.stellar.vault.domain.confirm_mnemonics

import io.reactivex.rxjava3.core.Single


interface ConfirmMnemonicsInteractor {
    fun createAndSaveSecretKey(mnemonics: CharArray) : Single<String>
}