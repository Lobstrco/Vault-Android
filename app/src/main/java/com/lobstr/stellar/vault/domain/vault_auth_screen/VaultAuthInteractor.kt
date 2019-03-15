package com.lobstr.stellar.vault.domain.vault_auth_screen

import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.Single


interface VaultAuthInteractor {

    fun getUserToken(): String?

    fun authorizeVault(): Single<List<Account>>

    fun registerFcm()

    fun getChallenge(): Single<String>

    fun submitChallenge(transaction: String): Single<String>

    fun getUserPublicKey(): String?

    fun getPhrases(): Single<String>

    fun confirmAccountHasSigners()

    fun getSignedAccounts(token: String): Single<List<Account>>
}