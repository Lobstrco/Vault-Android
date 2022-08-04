package com.lobstr.stellar.vault.presentation.home.account_name.manage

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.account.Account
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface ManageAccountsNamesView: MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun initRecycledView()

    @AddToEndSingle
    fun showAccountsNamesList(items: List<Account>?)

    @Skip
    fun scrollListToPosition(position: Int)

    @AddToEndSingle
    fun showEmptyState(show: Boolean)

    @AddToEndSingle
    fun showProgress(show: Boolean)

    @Skip
    fun showEditAccountDialog(address: String)

    @Skip
    fun copyToClipBoard(text: String)

    @Skip
    fun showAddAccountNameScreen()
}