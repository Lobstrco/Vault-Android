package com.lobstr.stellar.vault.domain.transaction_details

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountResult
import com.lobstr.stellar.vault.presentation.entities.stellar.SorobanBalanceData
import com.lobstr.stellar.vault.presentation.entities.stellar.SubmitTransactionResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import io.reactivex.rxjava3.core.Single
import org.stellar.sdk.AbstractTransaction


interface TransactionDetailsInteractor {

    fun hasMnemonics(): Boolean

    fun hasTangem(): Boolean

    fun getTangemCardId(): String?

    fun getUserPublicKey(): String?

    fun retrieveActualTransaction(transactionItem: TransactionItem, skip: Boolean = false): Single<TransactionItem>

    fun confirmTransactionOnHorizon(
        transaction: AbstractTransaction
    ): Single<SubmitTransactionResult>

    fun confirmTransactionOnServer(
        needAdditionalSignatures: Boolean,
        transactionStatus: Int?,
        hash: String,
        transaction: String
    ): Single<String>

    fun cancelTransaction(hash: String): Single<TransactionItem>

    fun getPhrases(): Single<String>

    fun isTrConfirmationEnabled(): Boolean

    fun getSignedAccounts(): Single<List<Account>>

    fun getAccountNames(): Map<String, String?>

    fun getTransactionSigners(
        xdr: String,
        sourceAccount: String
    ): Single<AccountResult>

    fun getStellarAccount(stellarAddress: String): Single<Account>

    fun signTransaction(transaction: String): Single<AbstractTransaction>

    fun createTransaction(transaction: String): Single<AbstractTransaction>

    fun getCountSequenceNumber(sourceAccount: String, sequenceNumber: Long): Single<Long>

    fun getSorobanBalanceChanges(
        sourceAccount: String,
        envelopXdr: String
    ): Single<List<SorobanBalanceData>>
}