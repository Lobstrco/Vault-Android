package com.lobstr.stellar.vault.presentation.home.transactions.submit_success

import androidx.annotation.ColorRes
import com.lobstr.stellar.vault.presentation.util.VibrateType
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface SuccessView : MvpView {

    @Skip
    fun vibrate(type: VibrateType)

    @AddToEndSingle
    fun setupXdr(xdr: String)

    @Skip
    fun finishScreen()

    @AddToEndSingle
    fun setDescription(description: String, @ColorRes color: Int)

    @AddToEndSingle
    fun showXdrContainer(show: Boolean)

    @AddToEndSingle
    fun showViewExplorer(show: Boolean)

    @Skip
    fun showHelpScreen()

    @Skip
    fun showWebPage(url: String)

    @Skip
    fun copyToClipBoard(text: String)
}