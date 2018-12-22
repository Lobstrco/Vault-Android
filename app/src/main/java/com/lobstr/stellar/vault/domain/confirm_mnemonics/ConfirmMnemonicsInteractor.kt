package com.lobstr.stellar.vault.domain.confirm_mnemonics

import io.reactivex.Single


interface ConfirmMnemonicsInteractor {
    fun createAndSaveSecretKey(mnemonics: CharArray) : Single<String>
}