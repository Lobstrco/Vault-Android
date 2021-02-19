package com.lobstr.stellar.vault.data.net.entities.account

import com.google.gson.annotations.SerializedName


data class ApiAppVersion(
        @SerializedName("current_version")
        val currentVersion: String?,
        @SerializedName("min_app_version")
        val minAppVersion: String?,
        @SerializedName("recommended")
        val recommended: String?
)