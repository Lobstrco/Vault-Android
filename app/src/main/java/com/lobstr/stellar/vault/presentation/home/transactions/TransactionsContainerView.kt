package com.lobstr.stellar.vault.presentation.home.transactions

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface TransactionsContainerView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun initViewPager()
}