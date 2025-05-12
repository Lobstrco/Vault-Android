package com.lobstr.stellar.vault.data.net.entities.transaction

import com.google.gson.annotations.SerializedName

data class ApiCancelTransactionsRequest(
    @SerializedName("status")
    val status: String?,
    @SerializedName("not_enough_signers_weight")
    val notEnoughSignersWeight: Boolean?,
    @SerializedName("submitted_at__isnull")
    val submittedAtIsNull: Boolean?,
    @SerializedName("sequence_outdated_at__isnull")
    val sequenceOutdatedAtIsNull: Boolean?
)