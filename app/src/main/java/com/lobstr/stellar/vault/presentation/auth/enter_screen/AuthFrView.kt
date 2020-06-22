package com.lobstr.stellar.vault.presentation.auth.enter_screen

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface AuthFrView : MvpView {

    @AddToEndSingle
    fun setMovementMethods()

    @Skip
    fun showBackUpScreen()

    @Skip
    fun showRestoreScreen()

    @Skip
    fun showTangemSetupScreen()

    @Skip
    fun showHelpScreen()

    @Skip
    fun showMessage(message: String?)
}