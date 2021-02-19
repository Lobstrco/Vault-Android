package com.lobstr.stellar.vault.presentation.entities.account


data class AppVersion(
    val currentVersion: String?,
    val minAppVersion: String?,
    val recommended: String?
)