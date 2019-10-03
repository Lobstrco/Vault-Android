package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import com.lobstr.stellar.vault.presentation.entities.account.Account

interface OnAccountItemListener {
    fun onAccountItemClick(account: Account)
    fun onAccountItemLongClick(account: Account)
}