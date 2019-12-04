package com.lobstr.stellar.vault.data.net.entities.account

import com.google.gson.annotations.SerializedName

data class ApiStellarAccount(
    @SerializedName("account_id") val accountId: String,
    @SerializedName("stellar_address") val stellarAddress: String
)