package com.lobstr.stellar.vault.domain.signed_account

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single

class SignedAccountInteractorImpl(
    private val accountRepository: AccountRepository,
    private val localDataRepository: LocalDataRepository,
    private val prefUtil: PrefsUtil
) : SignedAccountInteractor {

    override fun getSignedAccounts(): Single<List<Account>> =
        accountRepository.getSignedAccounts(AppUtil.getJwtToken(prefUtil.authToken))
            .doOnSuccess { prefUtil.accountSignersCount = it.size }

    override fun getAccountNames(): Map<String, String?> = localDataRepository.getAccountNames()

    override fun getStellarAccount(stellarAddress: String): Single<Account> =
        accountRepository.getStellarAccount(stellarAddress, "id")
}