package com.lobstr.stellar.vault.data.net.entities.vault_auth

import com.google.gson.annotations.SerializedName

data class ApiSubmitChallenge(
    @SerializedName("token") val token: String?
)