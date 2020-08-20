package com.lobstr.stellar.vault.domain.import_xdr

import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import io.reactivex.rxjava3.core.Single


class ImportXdrInteractorImpl(
    private val stellarRepository: StellarRepository
) : ImportXdrInteractor {
    override fun createTransactionItem(xdr: String): Single<TransactionItem> {
        return stellarRepository.createTransactionItem(xdr)
    }
}