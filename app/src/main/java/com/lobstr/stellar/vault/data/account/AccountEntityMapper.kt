package com.lobstr.stellar.vault.data.account

import com.lobstr.stellar.vault.data.net.entities.account.ApiAccount
import com.lobstr.stellar.vault.data.net.entities.account.ApiAccountConfig
import com.lobstr.stellar.vault.data.net.entities.account.ApiSignedAccountsResponse
import com.lobstr.stellar.vault.data.net.entities.account.ApiStellarAccount
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountConfig

class AccountEntityMapper {

    fun transformAccount(apiAccount: ApiAccount): Account {
        return Account(
            apiAccount.address
        )
    }

    fun transformSignedAccounts(response: ApiSignedAccountsResponse): List<Account> {
        if (response.results.isNullOrEmpty()) {
            return emptyList()
        }

        val accounts: MutableList<Account> = mutableListOf()
        response.results.forEach {
            accounts.add(Account(it.address))
        }

        return accounts
    }

    fun transformStellarAccount(apiStellarAccount: ApiStellarAccount): Account {
        return Account(
            apiStellarAccount.accountId,
            apiStellarAccount.stellarAddress
        )
    }

    fun transformAccountConfig(apiAccountConfig: ApiAccountConfig): AccountConfig {
        return AccountConfig(
            apiAccountConfig.spamProtectionEnabled
        )
    }
}