package com.lobstr.stellar.vault.data.transaction

import com.lobstr.stellar.vault.data.net.TransactionApi
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import io.reactivex.Single


class TransactionRepositoryImpl(
    private val transactionApi: TransactionApi,
    private val transactionEntityMapper: TransactionEntityMapper,
    private val rxErrorUtils: RxErrorUtils
) : TransactionRepository {

    override fun getTransactionList(token: String, type: String?, nextPageUrl: String?): Single<TransactionResult> {
        return when {
            type.isNullOrEmpty() && nextPageUrl.isNullOrEmpty() -> transactionApi.getTransactionList(token)
            type.isNullOrEmpty() && !nextPageUrl.isNullOrEmpty() -> transactionApi.getTransactionListByUrl(
                nextPageUrl,
                token
            )
            !type.isNullOrEmpty() && nextPageUrl.isNullOrEmpty() -> transactionApi.getTransactionList(type, token)
            else -> transactionApi.getTransactionListByUrl(nextPageUrl!!, token)
        }
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
            .map {
                transactionEntityMapper.transformTransactions(it)
            }
    }

    override fun submitSignedTransaction(token: String, submit: Boolean?, transaction: String): Single<String> {
        return transactionApi.submitSignedTransaction(token, submit, transaction)
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
            .map {
                it.xdr!!
            }
    }

    override fun markTransactionAsCancelled(token: String, hash: String): Single<TransactionItem> {
        return transactionApi.markTransactionAsCancelled(hash, token)
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
            .map {
                transactionEntityMapper.transformTransactionItem(it)
            }
    }
}