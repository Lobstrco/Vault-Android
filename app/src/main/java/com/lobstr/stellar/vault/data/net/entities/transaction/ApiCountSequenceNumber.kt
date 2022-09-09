package com.lobstr.stellar.vault.data.net.entities.transaction


import com.google.gson.annotations.SerializedName

data class ApiCountSequenceNumber(
    @SerializedName("count")
    val count: Long
)