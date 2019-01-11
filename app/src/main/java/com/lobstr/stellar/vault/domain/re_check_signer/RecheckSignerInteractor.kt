package com.lobstr.stellar.vault.domain.re_check_signer

import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.Single


interface RecheckSignerInteractor {

    fun getUserPublicKey(): String?

    fun getSignedAccounts(): Single<List<Account>>

    fun confirmAccountHasSigners()
}