package com.lobstr.stellar.vault.presentation.entities.account


data class Account(
    val address: String,
    var federation: String? = null,
    var name: String? = null,
    val weight: Int? = null,
    var isVaultAccount: Boolean? = null,
    val signed: Boolean? = null
)