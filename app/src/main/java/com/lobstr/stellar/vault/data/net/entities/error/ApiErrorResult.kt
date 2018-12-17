package com.lobstr.stellar.vault.data.net.entities.error

import com.google.gson.annotations.SerializedName

data class ApiErrorResult(
    @SerializedName("error") val error: String?
)