package com.lobstr.stellar.vault.domain.account

import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.Completable
import io.reactivex.Single


interface AccountRepository {

    fun updateAccountSigners(token: String, account: String): Completable

    fun getSignedAccounts(token: String): Single<List<Account>>

    fun getStellarAccount(accountId: String, type: String): Single<Account>
}