package com.lobstr.stellar.vault.data.error.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Error {
    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("detail")
    @Expose
    val detail: String? = null

    @SerializedName("error")
    @Expose
    val error: String? = null

    @SerializedName("non_field_errors")
    @Expose
    val nonFieldErrors: List<String>? = null
}