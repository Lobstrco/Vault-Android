package com.lobstr.stellar.vault.presentation.auth

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface AuthView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun showAuthFragment()

    @StateStrategyType(SkipStrategy::class)
    fun showBiometricSetUpFragment()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbar(@ColorRes toolbarColor: Int, @DrawableRes upArrow: Int, @ColorRes upArrowColor: Int)
}