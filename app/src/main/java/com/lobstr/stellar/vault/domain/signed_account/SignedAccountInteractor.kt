package com.lobstr.stellar.vault.domain.signed_account

import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.rxjava3.core.Single

interface SignedAccountInteractor {

    fun getSignedAccounts(): Single<List<Account>>

    fun getStellarAccount(stellarAddress: String): Single<Account>
}