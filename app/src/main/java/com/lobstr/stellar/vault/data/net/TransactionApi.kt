package com.lobstr.stellar.vault.data.net

import com.lobstr.stellar.vault.data.net.entities.transaction.ApiSubmitTransaction
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionResult
import io.reactivex.Single
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
}