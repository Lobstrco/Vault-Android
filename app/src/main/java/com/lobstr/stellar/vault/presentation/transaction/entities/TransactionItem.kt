package com.lobstr.stellar.vault.presentation.transaction.entities


data class TransactionItem(

    val cancelledAt: String?,

    val addedAt: String?,

    val xdr: String?,

    val signedAt: String?,

    val hash: String?,

    val getStatusDisplay: String?,

    val status: Int?
)