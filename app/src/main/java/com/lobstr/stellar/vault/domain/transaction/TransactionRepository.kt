package com.lobstr.stellar.vault.domain.transaction

import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import io.reactivex.Single


interface TransactionRepository {

    fun getTransactionList(token: String, type: String?, nextPageUrl: String?): Single<TransactionResult>

    fun submitSignedTransaction(
        token: String, submit: Boolean?, transaction: String
    ): Single<String>

    fun markTransactionAsCancelled(token: String, hash: String): Single<TransactionItem>
}