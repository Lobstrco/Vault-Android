package com.lobstr.stellar.vault.presentation.auth

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface AuthView : MvpView {
    @StateStrategyType(SkipStrategy::class)
    fun showAuthFragment()

    @StateStrategyType(SkipStrategy::class)
    fun popBackStack()

    @StateStrategyType(SkipStrategy::class)
    fun finishAuthActivity()
}