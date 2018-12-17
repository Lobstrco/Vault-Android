package com.lobstr.stellar.vault.data.net

import io.reactivex.Completable
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header


interface AccountApi {
    @GET("update-account-signers/")
    fun updateAccountSigners(
        @Header("Authorization") token: String,
        @Field("account") account: String
    ): Completable
}