package com.lobstr.stellar.vault.presentation.auth

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface AuthView : MvpView {

    @Skip
    fun showAuthFragment()

    @Skip
    fun showBiometricSetUpFragment()

    @AddToEndSingle
    fun setupToolbar(@ColorRes toolbarColor: Int, @DrawableRes upArrow: Int, @ColorRes upArrowColor: Int)
}