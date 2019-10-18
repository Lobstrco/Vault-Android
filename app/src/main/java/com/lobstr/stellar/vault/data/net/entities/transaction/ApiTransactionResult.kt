package com.lobstr.stellar.vault.data.net.entities.transaction

import com.google.gson.annotations.SerializedName

data class ApiTransactionResult(

    @SerializedName("next")
    val next: String?,

    @SerializedName("previous")
    val previous: String?,

    @SerializedName("count")
    val count: Int?,

    @SerializedName("results")
    val results: List<ApiTransactionItem?>?
)