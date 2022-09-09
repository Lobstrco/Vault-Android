package com.lobstr.stellar.vault.data.net

import com.lobstr.stellar.vault.data.net.entities.transaction.ApiCountSequenceNumber
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiSubmitTransaction
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*


interface TransactionApi {

    @GET("transactions/")
    fun getTransactionList(
        @Header("Authorization") token: String
    ): Single<ApiTransactionResult>

    @GET
    fun getTransactionListByUrl(
        @Url url: String,
        @Header("Authorization") token: String
    ): Single<ApiTransactionResult>

    @GET("transactions/{hash}/")
    fun getTransaction(
        @Path("hash") hash: String,
        @Header("Authorization") token: String
    ): Single<ApiTransactionItem>

    /**
     * Add some transaction type
     * @see com.lobstr.stellar.vault.presentation.util.Constant.TransactionType
     */
    @GET("transactions/{type}")
    fun getTransactionList(
        @Path("type") type: String,
        @Header("Authorization") token: String
    ): Single<ApiTransactionResult>

    /**
     * Called for submit transaction if needed additional signatures
     * Horizon: tx_bad_auth
     */
    @FormUrlEncoded
    @POST("transactions/")
    fun submitSignedTransaction(
        @Header("Authorization") token: String,
        @Field("xdr") transaction: String
    ): Single<ApiSubmitTransaction>

    /**
     * Called for submit transaction when No additional signatures needed
     */
    @FormUrlEncoded
    @POST("transactions/{hash}/submit/")
    fun markTransactionAsSubmitted(
        @Header("Authorization") token: String,
        @Path("hash") hash: String,
        @Field("xdr") transaction: String
    ): Single<ApiSubmitTransaction>

    @POST("transactions/{hash}/cancel/")
    fun markTransactionAsCancelled(
        @Path("hash") hash: String,
        @Header("Authorization") token: String
    ): Single<ApiTransactionItem>

    @POST("transactions/hide-all/")
    fun cancelTransactions(
        @Header("Authorization") token: String
    ): Completable

    @POST("transactions/hide-outdated/")
    fun cancelOutdatedTransactions(
        @Header("Authorization") token: String
    ): Completable

    /**
     * Return the number of transactions for the specified sequence.
     */
    @GET("count-sequence-number/{account}/{sequence_number}/")
    fun getCountSequenceNumber(
        @Header("Authorization") token: String,
        @Path("account") account: String,
        @Path("sequence_number") sequenceNumber: Long,
    ): Single<ApiCountSequenceNumber>
}