package com.lobstr.stellar.vault.data.transaction

import com.lobstr.stellar.vault.data.net.TransactionApi
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


class TransactionRepositoryImpl(
    private val transactionApi: TransactionApi,
    private val transactionEntityMapper: TransactionEntityMapper,
    private val rxErrorUtils: RxErrorUtils
) : TransactionRepository {

    override fun retrieveTransaction(token: String, hash: String): Single<TransactionItem> {
        return transactionApi.getTransaction(hash, token)
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
            .map {
                transactionEntityMapper.transformTransactionItem(it)
            }
    }

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

    override fun submitSignedTransaction(token: String, transaction: String): Single<String> {
        return transactionApi.submitSignedTransaction(token, transaction)
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
            .map {
                it.xdr!!
            }
    }

    override fun markTransactionAsSubmitted(token: String, hash: String, transaction: String): Single<String> {
        return transactionApi.markTransactionAsSubmitted(token, hash, transaction)
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

    override fun cancelTransactions(token: String): Completable {
        return transactionApi.cancelTransactions(token)
            .onErrorResumeNext { rxErrorUtils.handleCompletableRequestHttpError(it) }
    }

    override fun cancelOutdatedTransactions(token: String): Completable {
        return transactionApi.cancelOutdatedTransactions(token)
            .onErrorResumeNext { rxErrorUtils.handleCompletableRequestHttpError(it) }
    }

    override fun createTransaction(xdr: String): Single<TransactionItem> {
       return Single.fromCallable { transactionEntityMapper.transformTransactionXdr(xdr) }
    }

    override fun getCountSequenceNumber(token: String, account: String, sequenceNumber: Long): Single<Long> {
        return transactionApi.getCountSequenceNumber(token, account, sequenceNumber)
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
            .map {
                it.count
            }
    }
}