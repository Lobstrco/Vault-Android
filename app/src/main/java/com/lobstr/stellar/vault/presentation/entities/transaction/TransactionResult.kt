package com.lobstr.stellar.vault.presentation.entities.transaction


data class TransactionResult(

    val next: String?,

    val previous: String?,

    val count: Int,

    val results: List<TransactionItem>
)