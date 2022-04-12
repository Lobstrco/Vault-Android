package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface EditAccountView : MvpView {

    @AddToEndSingle
    fun setAccountActionButton(text: String?)

    @AddToEndSingle
    fun showClearAccountButton(show: Boolean)

    @Skip
    fun showSetNickNameFlow(publicKey: String)

    @Skip
    fun copyToClipBoard(text: String)

    @Skip
    fun openExplorer(url: String)

    @AddToEndSingle
    fun closeScreen()

    @AddToEndSingle
    fun showNetworkExplorerButton(show: Boolean)
}