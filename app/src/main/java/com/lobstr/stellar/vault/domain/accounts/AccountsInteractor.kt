package com.lobstr.stellar.vault.domain.accounts

import io.reactivex.rxjava3.core.Single

interface AccountsInteractor {

    fun getPublicKeyList(): List<Pair<String, Int>>

    fun getCurrentPublicKey(): String

    fun saveNewKeyData(key: String)

    fun generateNewKeyPair(): Single<String>

    fun getAccountNames(): Map<String, String?>
}