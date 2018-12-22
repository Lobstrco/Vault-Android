package com.lobstr.stellar.vault.domain.transaction

import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single

class TransactionInteractorImpl(
    private val transactionRepository: TransactionRepository,
    private val prefUtil: PrefsUtil
) : TransactionInteractor {

    override fun getTransactionList(nextPageUrl: String?): Single<TransactionResult> {
        return transactionRepository.getTransactionList(AppUtil.getJwtToken(prefUtil.authToken), null, nextPageUrl)
    }

    override fun getPendingTransactionList(nextPageUrl: String?): Single<TransactionResult> {
        return transactionRepository.getTransactionList(
            AppUtil.getJwtToken(prefUtil.authToken),
            Constant.TransactionType.PENDING,
            nextPageUrl
        )
    }

    override fun getInactiveTransactionList(nextPageUrl: String?): Single<TransactionResult> {
        return transactionRepository.getTransactionList(
            AppUtil.getJwtToken(prefUtil.authToken),
            Constant.TransactionType.INACTIVE,
            nextPageUrl
        )
    }
}