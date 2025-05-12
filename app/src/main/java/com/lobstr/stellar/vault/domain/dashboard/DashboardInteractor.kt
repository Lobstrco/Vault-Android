package com.lobstr.stellar.vault.domain.dashboard

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import io.reactivex.rxjava3.core.Single

interface DashboardInteractor {

    fun getPendingTransactionsList(): Single<TransactionResult>

    fun getUserPublicKey(): String?

    fun getUserPublicKeyIndex(): Int

    fun getSignedAccounts(): Single<List<Account>>

    fun getAccountNames(): Map<String, String?>

    fun getStellarAccount(stellarAddress: String): Single<Account>

    fun getSignersCount(): Int

    fun hasTangem(): Boolean
}