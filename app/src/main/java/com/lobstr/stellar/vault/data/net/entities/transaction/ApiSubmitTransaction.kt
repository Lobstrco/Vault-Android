package com.lobstr.stellar.vault.data.net.entities.transaction

import com.google.gson.annotations.SerializedName

data class ApiSubmitTransaction(
    @SerializedName("xdr")
    val xdr: String?
)