package com.lobstr.stellar.vault.domain.base

import io.reactivex.Single


interface BaseInteractor {

    fun getTangemCardId(): String?

    fun hasAuthToken(): Boolean

    fun getUserPublicKey(): String?

    fun getChallenge(): Single<String>

    fun authorizeVault(transaction: String): Single<String>
}