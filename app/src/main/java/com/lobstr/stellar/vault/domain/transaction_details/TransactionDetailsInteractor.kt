package com.lobstr.stellar.vault.domain.transaction_details

import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import io.reactivex.Single
import org.stellar.sdk.responses.SubmitTransactionResponse


interface TransactionDetailsInteractor {

    fun retrieveActualTransaction(hash: String): Single<TransactionItem>

    fun confirmTransactionOnHorizon(
        transaction: String
    ): Single<SubmitTransactionResponse>

    fun confirmTransactionOnServer(
        needAdditionalSignatures: Boolean,
        hash: String?,
        transaction: String
    ): Single<String>

    fun cancelTransaction(hash: String): Single<TransactionItem>

    fun getPhrases(): Single<String>
}