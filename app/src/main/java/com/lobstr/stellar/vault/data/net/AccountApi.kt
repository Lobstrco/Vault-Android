package com.lobstr.stellar.vault.data.net

import com.lobstr.stellar.vault.data.net.entities.account.ApiAccountConfig
import com.lobstr.stellar.vault.data.net.entities.account.ApiAppVersion
import com.lobstr.stellar.vault.data.net.entities.account.ApiSignedAccountsResponse
import com.lobstr.stellar.vault.data.net.entities.account.ApiStellarAccount
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*


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

    @GET("https://lobstr.co/api/resolve-federation/")
    fun getStellarAccount(
        @Query("q") accountId: String,
        @Query("type") type: String
    ): Single<ApiStellarAccount>

    @GET("account/config/")
    fun getAccountConfig(
        @Header("Authorization") token: String
    ): Single<ApiAccountConfig>

    @FormUrlEncoded
    @PUT("account/config/")
    fun updateAccountConfig(
        @Header("Authorization") token: String,
        @Field("spam_protection_enabled") spamProtectionEnabled: Boolean
    ): Single<ApiAccountConfig>

    @GET("version-control/android/")
    fun getAppVersion(): Single<ApiAppVersion>
}