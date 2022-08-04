package com.lobstr.stellar.vault.domain.account_name.manage


interface ManageAccountsNamesInteractor {
    fun getAccountNames(): Map<String, String?>
}