package com.lobstr.stellar.vault.presentation.auth

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface AuthView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun showAuthFragment()

    @StateStrategyType(SkipStrategy::class)
    fun showBiometricSetUpFragment()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbar(@ColorRes toolbarColor: Int, @DrawableRes upArrow: Int, @ColorRes upArrowColor: Int)
}