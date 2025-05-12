package com.lobstr.stellar.vault.domain.transaction

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class TransactionInteractorImpl(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val localDataRepository: LocalDataRepository,
    private val prefUtil: PrefsUtil
) : TransactionInteractor {

    override fun getFilteredTransactionsList(
        status: String,
        notEnoughSignersWeight: Boolean?,
        submittedAtIsNull: Boolean?,
        excludeOld: Boolean?,
        type: String?,
        nextPageUrl: String?,
        pageSize: Int?
    ): Single<TransactionResult> {
        return transactionRepository.getFilteredTransactionsList(
            AppUtil.getJwtToken(prefUtil.authToken),
            status,
            notEnoughSignersWeight,
            submittedAtIsNull,
            excludeOld,
            type,
            nextPageUrl,
            pageSize
        )
    }

    override fun cancelTransactions(
        status: String,
        notEnoughSignersWeight: Boolean?,
        submittedAtIsNull: Boolean?,
        sequenceOutdatedAtIsNull: Boolean?
    ): Completable {
        return transactionRepository.cancelTransactions(
            AppUtil.getJwtToken(prefUtil.authToken),
            status,
            notEnoughSignersWeight,
            submittedAtIsNull,
            sequenceOutdatedAtIsNull
        )
    }

    override fun cancelTransaction(hash: String): Single<TransactionItem> {
        return transactionRepository.markTransactionAsCancelled(
            AppUtil.getJwtToken(prefUtil.authToken),
            hash
        )
    }

    override fun getStellarAccount(stellarAddress: String): Single<Account> {
        return accountRepository.getStellarAccount(stellarAddress, "id")
    }

    override fun getAccountNames(): Map<String, String?> {
        return localDataRepository.getAccountNames()
    }
}