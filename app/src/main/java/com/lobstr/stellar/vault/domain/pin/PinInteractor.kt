package com.lobstr.stellar.vault.domain.pin

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface PinInteractor {

    fun checkPinValidation(pin: String): Single<Boolean>

    fun savePhrases(phrases: String): Completable

    fun savePin(pin: String): Completable

    fun getPhrases(): Single<String>

    fun accountHasToken(): Boolean

    fun isTouchIdSetUp(): Boolean

    fun isTouchIdEnabled(): Boolean

    fun getUserPublicKey(): String?

    fun clearUserData()
}