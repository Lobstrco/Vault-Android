package com.lobstr.stellar.vault.presentation.auth.backup

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface BackUpView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun showCreateMnemonicsScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()
}