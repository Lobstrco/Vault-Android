package com.lobstr.stellar.vault.presentation.auth.backup

import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface BackUpView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun showCreateMnemonicsScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()
}