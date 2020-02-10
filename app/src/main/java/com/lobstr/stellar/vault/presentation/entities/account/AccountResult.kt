package com.lobstr.stellar.vault.presentation.entities.account


data class AccountResult(
    val thresholds: Thresholds,
    var signers: List<Account>
)