package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter

import com.lobstr.stellar.vault.presentation.entities.account.Account

interface OnSignedAcoountItemClicked {
    fun onSignedAccountItemClick(account: Account)
}