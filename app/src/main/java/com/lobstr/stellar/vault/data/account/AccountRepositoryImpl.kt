package com.lobstr.stellar.vault.data.account

import com.lobstr.stellar.vault.data.net.AccountApi
import com.lobstr.stellar.vault.domain.account.AccountRepository
import io.reactivex.Completable


class AccountRepositoryImpl(private val accountApi: AccountApi) : AccountRepository {
    override fun updateAccountSigners(token: String, account: String): Completable {
        return accountApi.updateAccountSigners(token, account)
    }
}