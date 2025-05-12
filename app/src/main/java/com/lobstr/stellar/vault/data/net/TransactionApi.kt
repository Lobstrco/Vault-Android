package com.lobstr.stellar.vault.data.net

import com.lobstr.stellar.vault.data.net.entities.transaction.ApiCancelTransactionsRequest
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiCountSequenceNumber
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiSubmitTransaction
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*


interface TransactionApi {

    @GET("transactions/")
    fun getFilteredTransactionsList(
        @Header("Authorization") token: String,
        @Query("status") status: String?,
        @Query("not_enough_signers_weight") notEnoughSignersWeight: Boolean?,
        @Query("submitted_at__isnull") submittedAtIsNull: Boolean?,
        @Query("exclude_old") excludeOld: Boolean?,
        @Query("transaction_type") type: String?,
        @Query("page_size") pageSize: Int?
    ): Single<ApiTransactionResult>

    @GET
    fun getTransactionsListByUrl(
        @Url url: String,
        @Header("Authorization") token: String
    ): Single<ApiTransactionResult>

    @GET("transactions/{hash}/")
    fun getTransaction(
        @Header("Authorization") token: String,
        @Path("hash") hash: String,
    ): Single<ApiTransactionItem>

    /**
     * @param type Transaction type. [com.lobstr.stellar.vault.presentation.util.Constant.TransactionType].
     */
    @GET("transactions/{type}")
    fun getTypedTransactionsList(
        @Header("Authorization") token: String,
        @Path("type") type: String,
        @Query("page_size") pageSize: Int?
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
        @Header("Authorization") token: String,
        @Path("hash") hash: String,
    ): Single<ApiTransactionItem>

    @POST("transactions/hide-all/")
    fun cancelTransactions(
        @Header("Authorization") token: String,
        @Body body: ApiCancelTransactionsRequest
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