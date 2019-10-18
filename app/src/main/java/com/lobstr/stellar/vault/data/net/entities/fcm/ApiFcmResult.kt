package com.lobstr.stellar.vault.data.net.entities.fcm

import com.google.gson.annotations.SerializedName


data class ApiFcmResult(

    @SerializedName("id") val id: Long,

    @SerializedName("name") val name: String,

    @SerializedName("registration_id") val token: String,

    @SerializedName("device_id") val deviceId: String,

    @SerializedName("active") val isActive: Boolean,

    @SerializedName("date_created") val dateCreated: String,

    @SerializedName("type") val type: String
)