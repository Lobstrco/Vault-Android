package com.lobstr.stellar.vault.data.net.entities.account

import com.google.gson.annotations.SerializedName


data class ApiAccount(
    @SerializedName("address") val address: String
)