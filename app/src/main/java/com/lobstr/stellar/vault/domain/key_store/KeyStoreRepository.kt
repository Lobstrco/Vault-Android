package com.lobstr.stellar.vault.domain.key_store

import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem

interface KeyStoreRepository {

    fun encryptData(data: String, alias: String, aliasIV: String)

    fun decryptData(alias: String, aliasIV: String): String?

    fun decryptDataToMnemonicItems(alias: String, aliasIV: String): ArrayList<MnemonicItem>

    fun clearAll()

    fun clear(alias: String)
}