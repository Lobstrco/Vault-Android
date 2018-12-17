package com.lobstr.stellar.vault.data.transaction

import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionResult
import com.lobstr.stellar.vault.presentation.transaction.entities.TransactionItem
import com.lobstr.stellar.vault.presentation.transaction.entities.TransactionResult


class TransactionEntityMapper {

    fun transformTransactions(apiTransactionResult: ApiTransactionResult): TransactionResult {
        val transactions = mutableListOf<TransactionItem>()
        for (apiTransactionItem in apiTransactionResult.results!!) {
            transactions.add(transformTransactionItem(apiTransactionItem))
        }

        return TransactionResult(
            apiTransactionResult.next,
            apiTransactionResult.previous,
            apiTransactionResult.count,
            transactions
        )
    }

    fun transformTransactionItem(apiTransactionItem: ApiTransactionItem?): TransactionItem {
        return TransactionItem(
            apiTransactionItem?.cancelledAt,
            apiTransactionItem?.addedAt,
            apiTransactionItem?.xdr,
            apiTransactionItem?.signedAt,
            apiTransactionItem?.hash,
            apiTransactionItem?.getStatusDisplay,
            apiTransactionItem?.status
        )
    }
}