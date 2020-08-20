package com.lobstr.stellar.vault.presentation.auth.biometric

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface BiometricSetUpView : MvpView {

    @AddToEndSingle
    fun setWindowBackground()

    @AddToEndSingle
    fun windowLightNavigationBar(light: Boolean)

    @AddToEndSingle
    fun setupToolbar(
        showHomeAsUp: Boolean,
        @ColorRes toolbarColor: Int,
        @DrawableRes upArrow: Int,
        @ColorRes upArrowColor: Int
    )

    @Skip
    fun showVaultAuthScreen()

    @AddToEndSingle
    fun finishScreen()

    @Skip
    fun showBiometricInfoDialog(@StringRes titleRes: Int, @StringRes messageRes: Int)

    @Skip
    fun showBiometricDialog(show: Boolean)
}