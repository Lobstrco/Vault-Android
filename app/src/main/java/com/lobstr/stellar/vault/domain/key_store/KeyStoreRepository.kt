package com.lobstr.stellar.vault.domain.key_store

interface KeyStoreRepository {

    fun encryptData(data: String, alias: String, aliasIV: String)

    fun decryptData(alias: String, aliasIV: String): String?

    fun clearAll()

    fun clear(alias: String)
}