package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.account.Account
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface SignedAccountsView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun initRecycledView()

    @AddToEndSingle
    fun showProgress(show: Boolean)

    @AddToEndSingle
    fun showEmptyState(show: Boolean)

    @AddToEndSingle
    fun showErrorMessage(message: String)

    @AddToEndSingle
    fun notifyAdapter(accounts: List<Account>)

    @Skip
    fun scrollListToPosition(position: Int)

    @Skip
    fun showEditAccountDialog(address: String)

    @Skip
    fun copyToClipBoard(text: String)
}