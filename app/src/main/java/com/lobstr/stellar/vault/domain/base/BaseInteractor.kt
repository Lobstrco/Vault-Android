package com.lobstr.stellar.vault.domain.base

import io.reactivex.rxjava3.core.Single


interface BaseInteractor {

    fun getTangemCardId(): String?

    fun hasAuthToken(): Boolean

    fun getUserPublicKey(): String?

    fun getChallenge(): Single<String>

    fun authorizeVault(transaction: String): Single<String>

    fun hasEncryptedPin(): Boolean

    fun changePublicKeyInfo(publicKey: String)
}