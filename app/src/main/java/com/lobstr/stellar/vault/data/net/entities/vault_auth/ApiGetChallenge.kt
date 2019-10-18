package com.lobstr.stellar.vault.data.net.entities.vault_auth

import com.google.gson.annotations.SerializedName

data class ApiGetChallenge(
    @SerializedName("transaction") val transaction: String?
)