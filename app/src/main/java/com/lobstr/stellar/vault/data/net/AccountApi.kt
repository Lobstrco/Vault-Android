package com.lobstr.stellar.vault.data.net

import com.lobstr.stellar.vault.data.net.entities.account.ApiSignedAccountsResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header


interface AccountApi {
    @GET("update-account-signers/")
    fun updateAccountSigners(
        @Header("Authorization") token: String,
        @Field("account") account: String
    ): Completable

    @GET("signed-accounts/?page_size=all")
    fun getSignedAccounts(
        @Header("Authorization") token: String
    ): Single<ApiSignedAccountsResponse>
}