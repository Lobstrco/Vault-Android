package com.lobstr.stellar.vault.domain.signed_account

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single

class SignedAccountInteractorImpl(
    private val accountRepository: AccountRepository,
    private val prefUtil: PrefsUtil
) : SignedAccountInteractor {

    override fun getSignedAccounts(): Single<List<Account>> {
        return accountRepository.getSignedAccounts(AppUtil.getJwtToken(prefUtil.authToken))
            .doOnSuccess { prefUtil.accountSignersCount = it.size }
    }

    override fun clearAccountNames() {
        accountRepository.clearAccountNames()
    }

    override fun getAccountNames(): Map<String, String?> {
        return accountRepository.getAccountNames()
    }

    override fun getStellarAccount(stellarAddress: String): Single<Account> {
        return accountRepository.getStellarAccount(stellarAddress, "id")
    }
}