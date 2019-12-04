package com.lobstr.stellar.vault.presentation.splash

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface SplashView : MvpView {

    fun showAuthScreen()

    fun showPinScreen()
}