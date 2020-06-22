package com.lobstr.stellar.vault.data.net

import com.lobstr.stellar.vault.data.net.entities.fcm.ApiFcmResult
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.PUT


interface FcmApi {

    @FormUrlEncoded
    @PUT("device/")
    fun fcmDeviceRegistration(
        @Header("Authorization") token: String,
        @Field("type") type: String,
        @Field("registration_id") registrationId: String,
        @Field("active") active: Boolean
    ): Single<ApiFcmResult>
}