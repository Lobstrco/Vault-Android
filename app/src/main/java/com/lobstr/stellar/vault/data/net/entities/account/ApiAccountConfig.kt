package com.lobstr.stellar.vault.data.net.entities.account

import com.google.gson.annotations.SerializedName

data class ApiAccountConfig(
    @SerializedName("spam_protection_enabled")
    val spamProtectionEnabled: Boolean
)