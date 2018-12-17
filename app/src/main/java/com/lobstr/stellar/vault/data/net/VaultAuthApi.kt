package com.lobstr.stellar.vault.data.net

import com.lobstr.stellar.vault.data.net.entities.vault_auth.ApiGetChallenge
import com.lobstr.stellar.vault.data.net.entities.vault_auth.ApiSubmitChallenge
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface VaultAuthApi {

    @GET("authentication/")
    fun getChallenge(
        @Field("account") account: String
    ): Single<Response<ApiGetChallenge>>

    @FormUrlEncoded
    @POST("authentication/")
    fun submitChallenge(
        @Field("transaction") transaction: String
    ): Single<Response<ApiSubmitChallenge>>
}