package com.lobstr.stellar.vault.domain.import_xdr

import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import io.reactivex.rxjava3.core.Single


class ImportXdrInteractorImpl(
    private val transactionRepository: TransactionRepository
) : ImportXdrInteractor {
    override fun createTransactionItem(xdr: String): Single<TransactionItem> {
        return transactionRepository.createTransaction(xdr)
    }
}