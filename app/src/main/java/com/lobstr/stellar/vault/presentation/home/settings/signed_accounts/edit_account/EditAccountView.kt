package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

@Skip
interface EditAccountView : MvpView {
    fun copyToClipBoard(text: String)
    fun openExplorer(url: String)
}