package com.lobstr.stellar.vault.presentation.home.transactions.submit_error

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface ErrorView : MvpView {

    @Skip
    fun vibrate(pattern: LongArray)

    @AddToEndSingle
    fun setupErrorInfo(error: String)

    @Skip
    fun finishScreen()

    @Skip
    fun showHelpScreen()
}