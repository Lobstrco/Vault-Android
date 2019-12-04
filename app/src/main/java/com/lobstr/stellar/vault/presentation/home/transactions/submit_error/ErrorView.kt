package com.lobstr.stellar.vault.presentation.home.transactions.submit_error

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface ErrorView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun vibrate(pattern: LongArray)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupErrorInfo(error: String)

    @StateStrategyType(SkipStrategy::class)
    fun finishScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()
}