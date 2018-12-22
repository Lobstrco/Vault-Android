package com.lobstr.stellar.vault.data.transaction

import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult


class TransactionEntityMapper {

    fun transformTransactions(apiTransactionResult: ApiTransactionResult?): TransactionResult {
        if (apiTransactionResult == null) {
            return TransactionResult(
                null,
                null,
                0,
                mutableListOf()
            )
        }

        val transactions = mutableListOf<TransactionItem>()
        for (apiTransactionItem in apiTransactionResult.results!!) {
            if (apiTransactionItem != null) {
                transactions.add(transformTransactionItem(apiTransactionItem))
            }
        }

        return TransactionResult(
            apiTransactionResult.next,
            apiTransactionResult.previous,
            apiTransactionResult.count ?: 0,
            transactions
        )
    }

    fun transformTransactionItem(apiTransactionItem: ApiTransactionItem): TransactionItem {
        return TransactionItem(
            apiTransactionItem.cancelledAt,
            apiTransactionItem.addedAt,
            apiTransactionItem.xdr,
            apiTransactionItem.signedAt,
            apiTransactionItem.hash!!,
            apiTransactionItem.getStatusDisplay,
            apiTransactionItem.status
        )
    }
}