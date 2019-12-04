package com.lobstr.stellar.vault.domain.dashboard

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import io.reactivex.Single

interface DashboardInteractor {
    fun getPendingTransactionList(nextPageUrl: String?): Single<TransactionResult>

    fun getUserPublicKey(): String

    fun getSignedAccounts(): Single<List<Account>>

    fun getStellarAccount(stellarAddress: String): Single<Account>

    fun getSignersCount(): Int
}