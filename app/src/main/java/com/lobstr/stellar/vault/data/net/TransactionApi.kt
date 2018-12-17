package com.lobstr.stellar.vault.data.net

import com.lobstr.stellar.vault.data.net.entities.transaction.ApiSubmitTransaction
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionResult
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*


interface TransactionApi {

    /**
     * Add some transaction type
     * @see com.lobstr.stellar.vault.presentation.util.Constant.TransactionType
     */
    @GET("transactions/{type}")
    fun getTransactionList(
        @Path("type") type: String?,
        @Header("Authorization") token: String,
        @Query("page") page: String?
    ): Single<Response<ApiTransactionResult>>

    @FormUrlEncoded
    @POST("transactions/")
    fun submitSignedTransaction(
        @Header("Authorization") token: String,
        @Field("submit") submit: Boolean?,
        @Field("xdr") transaction: String
    ): Single<Response<ApiSubmitTransaction>>

    @POST("transactions/{hash}/cancel/")
    fun markTransactionAsCancelled(
        @Path("hash") hash: String,
        @Header("Authorization") token: String
    ): Single<Response<ApiTransactionItem>>
}