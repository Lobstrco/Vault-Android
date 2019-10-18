package com.lobstr.stellar.vault.data.net.entities.transaction

import com.google.gson.annotations.SerializedName

data class ApiTransactionItem(

    @SerializedName("cancelled_at")
    val cancelledAt: String?,

    @SerializedName("added_at")
    val addedAt: String?,

    @SerializedName("xdr")
    val xdr: String?,

    @SerializedName("signed_at")
    val signedAt: String?,

    @SerializedName("hash")
    val hash: String?,

    @SerializedName("get_status_display")
    val getStatusDisplay: String?,

    @SerializedName("status")
    val status: Int?,

    @SerializedName("sequence_outdated_at")
    val sequenceOutdatedAt: String?
)