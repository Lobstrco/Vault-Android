package com.lobstr.stellar.vault.presentation.splash

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface SplashView : MvpView {

    @Skip
    fun setWindowInset()

    @AddToEndSingle
    fun showAuthScreen()

    @AddToEndSingle
    fun showPinScreen()
}