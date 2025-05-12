package com.lobstr.stellar.vault.domain.transaction

import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


interface TransactionRepository {

    fun retrieveTransaction(token: String, hash: String): Single<TransactionItem>

    fun getTypedTransactionsList(token: String, type: String, nextPageUrl: String?, pageSize: Int?): Single<TransactionResult>

    /**
     * @param status Transaction status. [com.lobstr.stellar.vault.presentation.util.Constant.Transaction.Status].
     * @param type Transaction type. [com.lobstr.stellar.tsmapper.presentation.util.Constant.TransactionType].
     */
    fun getFilteredTransactionsList(
        token: String,
        status: String?,
        notEnoughSignersWeight: Boolean? = null,
        submittedAtIsNull: Boolean? = null,
        excludeOld: Boolean? = null,
        type: String? = null,
        nextPageUrl: String? = null,
        pageSize: Int? = null
    ): Single<TransactionResult>

    fun submitSignedTransaction(
        token: String, transaction: String
    ): Single<String>

    fun markTransactionAsSubmitted(
        token: String, hash: String, transaction: String
    ): Single<String>

    fun markTransactionAsCancelled(token: String, hash: String): Single<TransactionItem>

    fun cancelTransactions(
        token: String,
        status: String?,
        notEnoughSignersWeight: Boolean?,
        submittedAtIsNull: Boolean?,
        sequenceOutdatedAtIsNull: Boolean?
    ): Completable

    fun cancelOutdatedTransactions(token: String): Completable

    fun createTransaction(xdr: String): Single<TransactionItem>

    /**
     * @param account Source Account.
     * @param sequenceNumber Transaction's sequence number.
     * @return The number of transactions for the specified sequence.
     */
    fun getCountSequenceNumber(token: String, account: String, sequenceNumber: Long): Single<Long>
}