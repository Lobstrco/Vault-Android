package com.lobstr.stellar.vault.data.net

import com.lobstr.stellar.vault.data.net.entities.vault_auth.ApiGetChallenge
import com.lobstr.stellar.vault.data.net.entities.vault_auth.ApiSubmitChallenge
import io.reactivex.Single
import retrofit2.http.*


interface VaultAuthApi {

    @GET("authentication/")
    fun getChallenge(
        @Query("account") account: String?
    ): Single<ApiGetChallenge>

    @FormUrlEncoded
    @POST("authentication/")
    fun submitChallenge(
        @Field("transaction") transaction: String
    ): Single<ApiSubmitChallenge>
}