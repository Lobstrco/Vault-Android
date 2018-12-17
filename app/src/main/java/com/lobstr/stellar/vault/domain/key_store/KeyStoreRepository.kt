package com.lobstr.stellar.vault.domain.key_store

import java.security.KeyPair

interface KeyStoreRepository {

    fun encryptSecretKey(pin: String, secretKey: String): String

    fun getAsymmetricKeyPair(pin: String): KeyPair?

    fun decryptSecretKey(pin: String, secretKey: String): String

    fun clear()
}