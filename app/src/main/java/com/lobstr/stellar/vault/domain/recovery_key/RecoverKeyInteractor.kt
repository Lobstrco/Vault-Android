package com.lobstr.stellar.vault.domain.recovery_key

import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.rxjava3.core.Single

interface RecoverKeyInteractor {

    fun createAndSaveSecretKey(mnemonics: String): Single<String>

    fun createAdditionalPublicKey(mnemonics: String, position: Int): Single<String>

    fun checkAccount(publicKey: String): Single<List<Account>>

    fun savePublicKeyToList(key: String, index: Int)
}