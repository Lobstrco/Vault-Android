package com.lobstr.stellar.vault.domain.transaction

import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import io.reactivex.Single


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
}