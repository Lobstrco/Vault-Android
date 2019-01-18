package com.lobstr.stellar.vault.presentation.auth.enter_screen

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface AuthFrView : MvpView {

    fun setMovementMethods()

    fun showBackUpScreen()

    fun showRestoreScreen()
}