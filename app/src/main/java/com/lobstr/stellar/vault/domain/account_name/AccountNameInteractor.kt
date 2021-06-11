package com.lobstr.stellar.vault.domain.account_name


interface AccountNameInteractor {
    fun getAccountName(publicKey: String): String?
    fun saveAccountName(publicKey: String, name: String?)
}