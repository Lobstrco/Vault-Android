package com.lobstr.stellar.vault.presentation.home.account_name

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface AccountNameView : MvpView {

    @AddToEndSingle
    fun setupTitle(title: String?)

    @Skip
    fun setAccountName(accountName: String?)

    @Skip
    fun closeScreen()
}