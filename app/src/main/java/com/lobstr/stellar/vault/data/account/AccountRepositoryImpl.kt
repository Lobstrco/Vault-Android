package com.lobstr.stellar.vault.data.account

import com.lobstr.stellar.vault.data.net.AccountApi
import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.Completable
import io.reactivex.Single


class AccountRepositoryImpl(
    private val accountApi: AccountApi, private val accountEntityMapper: AccountEntityMapper,
    private val rxErrorUtils: RxErrorUtils
) :
    AccountRepository {

    override fun updateAccountSigners(token: String, account: String): Completable {
        return accountApi.updateAccountSigners(token, account)
            .onErrorResumeNext { rxErrorUtils.handleCompletableRequestHttpError(it) }
    }

    override fun getSignedAccounts(token: String): Single<List<Account>> {
        return accountApi.getSignedAccounts(token)
            .map { accountEntityMapper.transformSignedAccounts(it) }
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
    }
}