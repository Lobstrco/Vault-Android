package com.lobstr.stellar.vault.data.transaction

import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.Firebase
import com.lobstr.stellar.tsmapper.data.transaction.TsMapper
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.IMPORT_XDR

class TransactionEntityMapper(private val tsMapper: TsMapper) {

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
                try {
                    transactions.add(transformTransactionItem(apiTransactionItem))
                } catch (exc: Exception) {
                    // Skip Unmappable transactions.
                    Firebase.crashlytics.recordException(exc)
                }
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
            apiTransactionItem.addedAt,
            apiTransactionItem.hash!!,
            apiTransactionItem.status,
            apiTransactionItem.sequenceOutdatedAt,
            tsMapper.getTransaction(
                apiTransactionItem.xdr!!,
                apiTransactionItem.transactionType
            )
        )
    }

    fun transformTransactionXdr(xdr: String): TransactionItem {
        return tsMapper.getTransaction(xdr).let {
            TransactionItem(
                "",
                it.hash,
                IMPORT_XDR,
                null,
                it
            )
        }
    }
}