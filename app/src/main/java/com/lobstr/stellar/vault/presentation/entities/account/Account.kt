package com.lobstr.stellar.vault.presentation.entities.account


data class Account(val address: String, var federation: String? = null,val signed: Boolean? = null)