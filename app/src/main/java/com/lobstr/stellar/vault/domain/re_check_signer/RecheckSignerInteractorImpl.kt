package com.lobstr.stellar.vault.domain.re_check_signer

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single


class RecheckSignerInteractorImpl(
    private val accountRepository: AccountRepository,
    private val prefsUtil: PrefsUtil
) : RecheckSignerInteractor {

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }

    override fun getSignedAccounts(): Single<List<Account>> {
        return accountRepository.getSignedAccounts(AppUtil.getJwtToken(prefsUtil.authToken))
            .doOnSuccess { prefsUtil.accountSignersCount = it.size }
    }

    override fun confirmAccountHasSigners() {
        prefsUtil.accountHasSigners = true
    }
}