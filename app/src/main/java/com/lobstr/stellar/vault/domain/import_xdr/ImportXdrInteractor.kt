package com.lobstr.stellar.vault.domain.import_xdr

import io.reactivex.Single
import org.stellar.sdk.responses.SubmitTransactionResponse


interface ImportXdrInteractor {

    fun confirmTransactionOnHorizon(
        transaction: String
    ): Single<SubmitTransactionResponse>

    fun confirmTransactionOnServer(
        needAdditionalSignatures: Boolean,
        hash: String?,
        transaction: String
    ): Single<String>

    fun getPhrases(): Single<String>
}