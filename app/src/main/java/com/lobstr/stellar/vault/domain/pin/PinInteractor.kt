package com.lobstr.stellar.vault.domain.pin

import io.reactivex.Completable
import io.reactivex.Single
import java.security.KeyPair

interface PinInteractor {

    fun checkPinValidation(pin: String): Single<KeyPair>

    fun saveSecretKey(pin: String, secretKey: String): Completable

    fun getSecretKey(pin: String): Single<String>

    fun clear()
}