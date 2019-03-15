package com.lobstr.stellar.vault.presentation.auth.enter_screen

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

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