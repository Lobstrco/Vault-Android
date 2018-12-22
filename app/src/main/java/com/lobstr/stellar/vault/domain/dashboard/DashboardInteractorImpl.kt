package com.lobstr.stellar.vault.domain.dashboard

import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single

class DashboardInteractorImpl(
    private val transactionRepository: TransactionRepository,
    private val prefUtil: PrefsUtil
) : DashboardInteractor {

    override fun getPendingTransactionList(nextPageUrl: String?): Single<TransactionResult> {
        return transactionRepository.getTransactionList(
            AppUtil.getJwtToken(prefUtil.authToken),
            Constant.TransactionType.PENDING,
            nextPageUrl
        )
    }
}