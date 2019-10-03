package com.lobstr.stellar.vault.presentation.home.transactions.submit_success

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface SuccessView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun vibrate(pattern: LongArray)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupXdr(xdr: String)

    @StateStrategyType(SkipStrategy::class)
    fun finishScreen()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setAdditionalSignaturesInfoEnabled(enabled: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun copyToClipBoard(text: String)

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()
}