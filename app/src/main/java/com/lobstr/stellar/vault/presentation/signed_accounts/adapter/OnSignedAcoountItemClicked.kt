package com.lobstr.stellar.vault.presentation.signed_accounts.adapter

import com.lobstr.stellar.vault.presentation.entities.account.Account

interface OnSignedAcoountItemClicked {
    fun onSignedAccountItemClick(account: Account)
}