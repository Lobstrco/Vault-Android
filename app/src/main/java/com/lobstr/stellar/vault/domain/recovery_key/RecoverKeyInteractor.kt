package com.lobstr.stellar.vault.domain.recovery_key

import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.rxjava3.core.Single

interface RecoverKeyInteractor {

    fun createAndSaveSecretKey(mnemonics: CharArray): Single<String>

    fun createAdditionalPublicKey(mnemonics: CharArray, position: Int): Single<String>

    fun checkAccount(publicKey: String): Single<List<Account>>

    fun savePublicKeyToList(key: String, index: Int)
}