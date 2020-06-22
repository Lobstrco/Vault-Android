package com.lobstr.stellar.vault.presentation.splash

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface SplashView : MvpView {

    @AddToEndSingle
    fun showAuthScreen()

    @AddToEndSingle
    fun showPinScreen()

    @AddToEndSingle
    fun showHomeScreen()

    @AddToEndSingle
    fun showVaultAuthScreen()

    @AddToEndSingle
    fun showFlavorDialog(flavor: String)
}