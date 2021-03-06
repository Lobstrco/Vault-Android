package com.lobstr.stellar.vault.presentation.home.transactions.submit_success

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface SuccessView : MvpView {

    @Skip
    fun vibrate(pattern: LongArray)

    @AddToEndSingle
    fun setupXdr(xdr: String)

    @Skip
    fun finishScreen()

    @AddToEndSingle
    fun setAdditionalSignaturesInfoEnabled(enabled: Boolean)

    @AddToEndSingle
    fun showXdrContainer(show: Boolean)

    @Skip
    fun showHelpScreen(userId: String?)

    @Skip
    fun showWebPage(url: String)

    @Skip
    fun copyToClipBoard(text: String)
}