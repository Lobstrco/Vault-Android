package com.lobstr.stellar.vault.presentation.auth.enter_screen

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface AuthFrView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setMovementMethods()

    @StateStrategyType(SkipStrategy::class)
    fun showBackUpScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showRestoreScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()
}