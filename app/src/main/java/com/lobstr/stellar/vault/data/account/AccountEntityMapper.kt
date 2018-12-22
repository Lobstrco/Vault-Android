package com.lobstr.stellar.vault.data.account

import com.lobstr.stellar.vault.data.net.entities.account.ApiAccount
import com.lobstr.stellar.vault.presentation.entities.account.Account


class AccountEntityMapper {
    fun transformAccount(apiAccount: ApiAccount): Account {
        return Account(
            apiAccount.address
        )
    }
}