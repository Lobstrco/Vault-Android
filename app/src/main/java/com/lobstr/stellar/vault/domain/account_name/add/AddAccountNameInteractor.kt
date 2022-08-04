package com.lobstr.stellar.vault.domain.account_name.add

interface AddAccountNameInteractor {
    fun getAccountNames(): Map<String, String?>
    fun saveAccountName(publicKey: String, name: String?)
}