package com.lobstr.stellar.vault.domain.import_xdr

import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import io.reactivex.rxjava3.core.Single


interface ImportXdrInteractor {
    fun createTransactionItem(
        xdr: String
    ): Single<TransactionItem>
}