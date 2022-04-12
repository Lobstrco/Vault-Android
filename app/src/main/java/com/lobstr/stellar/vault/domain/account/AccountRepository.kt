package com.lobstr.stellar.vault.domain.account

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountConfig
import com.lobstr.stellar.vault.presentation.entities.account.AppVersion
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface AccountRepository {

    fun updateAccountSigners(token: String, account: String): Completable

    fun getSignedAccounts(token: String): Single<List<Account>>

    fun getStellarAccount(accountId: String, type: String): Single<Account>

    fun getAccountConfig(token: String): Single<AccountConfig>

    fun updateAccountConfig(token: String, spamProtectionEnabled: Boolean): Single<AccountConfig>

    fun getAppVersion(): Single<AppVersion>
}