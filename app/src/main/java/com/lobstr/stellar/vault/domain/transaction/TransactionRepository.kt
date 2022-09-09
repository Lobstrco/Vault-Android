package com.lobstr.stellar.vault.domain.transaction

import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


interface TransactionRepository {

    fun retrieveTransaction(token: String, hash: String): Single<TransactionItem>

    fun getTransactionList(token: String, type: String?, nextPageUrl: String?): Single<TransactionResult>

    fun submitSignedTransaction(
        token: String, transaction: String
    ): Single<String>

    fun markTransactionAsSubmitted(
        token: String, hash: String, transaction: String
    ): Single<String>

    fun markTransactionAsCancelled(token: String, hash: String): Single<TransactionItem>

    fun cancelTransactions(token: String): Completable

    fun cancelOutdatedTransactions(token: String): Completable

    fun createTransaction(xdr: String): Single<TransactionItem>

    /**
     * @param account Source Account.
     * @param sequenceNumber Transaction's sequence number.
     * @return The number of transactions for the specified sequence.
     */
    fun getCountSequenceNumber(token: String, account: String, sequenceNumber: Long): Single<Long>
}