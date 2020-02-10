package com.lobstr.stellar.vault.presentation.splash

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SplashView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun setWindowInset()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showAuthScreen()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPinScreen()
}