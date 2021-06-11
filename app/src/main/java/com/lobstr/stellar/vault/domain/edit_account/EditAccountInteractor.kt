package com.lobstr.stellar.vault.domain.edit_account


interface EditAccountInteractor {
    fun getAccountName(publicKey: String): String?
    fun clearAccountName(publicKey: String)
}
