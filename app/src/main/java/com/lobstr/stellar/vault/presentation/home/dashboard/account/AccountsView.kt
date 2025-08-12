package com.lobstr.stellar.vault.presentation.home.dashboard.account

import com.lobstr.stellar.vault.presentation.entities.account.AccountDialogItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface AccountsView: MvpView {

    @AddToEndSingle
    fun showAddAccountButton(visible: Boolean)

    @AddToEndSingle
    fun initList(list: List<AccountDialogItem>, scrollPosition: Int)

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @Skip
    fun showErrorMessage(message: String?)

    @AddToEndSingle
    fun notifyHomeActivity()

    @AddToEndSingle
    fun dismissDialog()
}