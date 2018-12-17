package com.lobstr.stellar.vault.data.transaction

import com.lobstr.stellar.vault.data.net.TransactionApi
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiSubmitTransaction
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionResult
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.transaction.entities.TransactionItem
import com.lobstr.stellar.vault.presentation.transaction.entities.TransactionResult
import io.reactivex.Single


class TransactionRepositoryImpl(
    private val transactionApi: TransactionApi,
    private val transactionEntityMapper: TransactionEntityMapper
) : TransactionRepository {

    override fun getTransactionList(token: String, type: String?, page: String?): Single<TransactionResult> {
        return transactionApi.getTransactionList(type, token, page)
            .map { response ->
                val apiTransactionResultResponse: ApiTransactionResult = response.body()!!
                transactionEntityMapper.transformTransactions(apiTransactionResultResponse)
            }
    }

    override fun submitSignedTransaction(token: String, submit: Boolean?, transaction: String): Single<String> {
        return transactionApi.submitSignedTransaction(token, submit, transaction)
            .map { response ->
                val apiSubmitTransactionResppnse: ApiSubmitTransaction = response.body()!!
                apiSubmitTransactionResppnse.xdr!!
            }
    }

    override fun markTransactionAsCancelled(token: String, hash: String): Single<TransactionItem> {
        return transactionApi.markTransactionAsCancelled(hash, token)
            .map { response ->
                val apiTransactionItemResponse: ApiTransactionItem = response.body()!!
                transactionEntityMapper.transformTransactionItem(apiTransactionItemResponse)
            }
    }
}