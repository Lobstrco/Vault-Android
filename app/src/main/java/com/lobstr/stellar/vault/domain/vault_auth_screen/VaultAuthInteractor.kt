package com.lobstr.stellar.vault.domain.vault_auth_screen

import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.Single


interface VaultAuthInteractor {

    fun hasMnemonics(): Boolean

    fun hasTangem(): Boolean

    fun getTangemCardId(): String?

    fun getUserToken(): String?

    fun authorizeVault(transaction: String): Single<String>

    fun authorizeVault(): Single<String>

    fun registerFcm()

    fun getChallenge(): Single<String>

    fun submitChallenge(transaction: String): Single<String>

    fun getUserPublicKey(): String?

    fun getPhrases(): Single<String>

    fun confirmAccountHasSigners()

    fun getSignedAccounts(token: String): Single<List<Account>>

    fun clearUserData()
}