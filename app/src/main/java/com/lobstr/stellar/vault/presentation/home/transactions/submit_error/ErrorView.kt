package com.lobstr.stellar.vault.presentation.home.transactions.submit_error

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface ErrorView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun vibrate(pattern: LongArray)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupErrorInfo(error: String)

    @StateStrategyType(SkipStrategy::class)
    fun finishScreen()
}