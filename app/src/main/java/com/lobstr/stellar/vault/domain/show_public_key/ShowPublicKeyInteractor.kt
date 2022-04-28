package com.lobstr.stellar.vault.domain.show_public_key

interface ShowPublicKeyInteractor {

    fun getUserPublicKeyIndex(key: String): Int

    fun getAccountNames(): Map<String, String?>

    fun hasTangem(): Boolean
}