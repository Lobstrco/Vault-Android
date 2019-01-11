package com.lobstr.stellar.vault.data.net.entities.account

import com.google.gson.annotations.SerializedName


data class ApiSignedAccountsResponse(

    @SerializedName("next")
    val next: String?,

    @SerializedName("previous")
    val previous: String?,

    @SerializedName("count")
    val count: Int?,

    @SerializedName("results")
    val results: List<ApiAccount>?
)