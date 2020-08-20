package com.lobstr.stellar.vault.domain.transaction

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface TransactionInteractor {

    fun getTransactionList(nextPageUrl: String?): Single<TransactionResult>

    fun getPendingTransactionList(nextPageUrl: String?): Single<TransactionResult>

    fun getInactiveTransactionList(nextPageUrl: String?): Single<TransactionResult>

    fun cancelOutdatedTransactions(): Completable

    fun getStellarAccount(stellarAddress: String): Single<Account>
}