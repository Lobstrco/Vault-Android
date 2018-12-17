package com.lobstr.stellar.vault.domain.transaction

import com.lobstr.stellar.vault.presentation.transaction.entities.TransactionItem
import com.lobstr.stellar.vault.presentation.transaction.entities.TransactionResult
import io.reactivex.Single


interface TransactionRepository {

    fun getTransactionList(token: String, type: String?, page: String?): Single<TransactionResult>

    fun submitSignedTransaction(
        token: String, submit: Boolean?, transaction: String
    ): Single<String>

    fun markTransactionAsCancelled(token: String, hash: String): Single<TransactionItem>
}