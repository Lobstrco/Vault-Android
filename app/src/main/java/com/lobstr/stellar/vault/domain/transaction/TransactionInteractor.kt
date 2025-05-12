package com.lobstr.stellar.vault.domain.transaction

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface TransactionInteractor {

    fun getFilteredTransactionsList(
        status: String,
        notEnoughSignersWeight: Boolean?,
        submittedAtIsNull: Boolean?,
        excludeOld: Boolean?,
        type: String?,
        nextPageUrl: String?,
        pageSize: Int?
    ): Single<TransactionResult>

    fun cancelTransactions(
        status: String,
        notEnoughSignersWeight: Boolean?,
        submittedAtIsNull: Boolean?,
        sequenceOutdatedAtIsNull: Boolean?
    ): Completable

    fun cancelTransaction(hash: String): Single<TransactionItem>

    fun getStellarAccount(stellarAddress: String): Single<Account>

    fun getAccountNames(): Map<String, String?>
}