package com.lobstr.stellar.vault.domain.transaction

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class TransactionInteractorImpl(
    private val accountRepository: AccountRepository,
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

    override fun cancelTransactions(): Completable {
        return transactionRepository.cancelTransactions(AppUtil.getJwtToken(prefUtil.authToken))
    }

    override fun cancelOutdatedTransactions(): Completable {
        return transactionRepository.cancelOutdatedTransactions(AppUtil.getJwtToken(prefUtil.authToken))
    }

    override fun getStellarAccount(stellarAddress: String): Single<Account> {
        return accountRepository.getStellarAccount(stellarAddress, "id")
    }

    override fun getAccountNames(): Map<String, String?> {
        return accountRepository.getAccountNames()
    }
}