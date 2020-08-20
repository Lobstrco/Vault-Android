package com.lobstr.stellar.vault.domain.dashboard

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single

class DashboardInteractorImpl(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val prefsUtil: PrefsUtil
) : DashboardInteractor {

    override fun getPendingTransactionList(nextPageUrl: String?): Single<TransactionResult> {
        return transactionRepository.getTransactionList(
            AppUtil.getJwtToken(prefsUtil.authToken),
            Constant.TransactionType.PENDING,
            nextPageUrl
        )
    }

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }

    override fun getSignedAccounts(): Single<List<Account>> {
        return accountRepository.getSignedAccounts(AppUtil.getJwtToken(prefsUtil.authToken))
            .doOnSuccess { prefsUtil.accountSignersCount = it.size }
    }

    override fun getSignersCount(): Int {
        return prefsUtil.accountSignersCount
    }

    override fun getStellarAccount(stellarAddress: String): Single<Account> {
        return accountRepository.getStellarAccount(stellarAddress, "id")
    }

    override fun hasTangem(): Boolean {
        return !prefsUtil.tangemCardId.isNullOrEmpty()
    }
}