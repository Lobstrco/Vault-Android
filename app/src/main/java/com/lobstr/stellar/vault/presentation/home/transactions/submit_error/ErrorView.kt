package com.lobstr.stellar.vault.presentation.home.transactions.submit_error

import com.lobstr.stellar.vault.presentation.util.VibrateType
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface ErrorView : MvpView {

    @Skip
    fun vibrate(type: VibrateType)

    @AddToEndSingle
    fun setupXdr(xdr: String)

    @AddToEndSingle
    fun setupErrorInfo(error: String, showViewDetails: Boolean)

    @Skip
    fun finishScreen()

    @Skip
    fun showHelpScreen()

    @Skip
    fun showWebPage(url: String)

    @Skip
    fun copyToClipBoard(text: String)

    @Skip
    fun showErrorDetails(details: String)
}