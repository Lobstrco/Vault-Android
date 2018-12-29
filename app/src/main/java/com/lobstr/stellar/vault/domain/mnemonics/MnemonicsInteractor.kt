package com.lobstr.stellar.vault.domain.mnemonics

import io.reactivex.Single


interface MnemonicsInteractor {
    fun getPhrases(): Single<String>
}