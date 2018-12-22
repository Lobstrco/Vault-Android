package com.lobstr.stellar.vault.presentation.splash

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface SplashView : MvpView {

    fun showAuthScreen()

    fun showPinScreen()
}