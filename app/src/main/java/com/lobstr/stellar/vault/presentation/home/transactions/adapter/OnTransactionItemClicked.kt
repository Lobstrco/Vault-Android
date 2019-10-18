package com.lobstr.stellar.vault.presentation.home.transactions.adapter

import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem

interface OnTransactionItemClicked {
    fun onTransactionItemClick(transactionItem: TransactionItem)
}