package com.lobstr.stellar.vault.presentation.pin

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface PinView : MvpView {

    @AddToEndSingle
    fun showTitle(@StringRes title: Int)

    @AddToEndSingle
    fun setScreenStyle(style: Int)

    @Skip
    fun resetPin()

    @Skip
    fun showHomeScreen()

    @Skip
    fun showVaultAuthScreen()

    @Skip
    fun showBiometricSetUpScreen()

    @Skip
    fun showErrorMessage(@StringRes message: Int)

    @Skip
    fun finishScreenWithResult(resultCode: Int)

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @Skip
    fun showBiometricDialog(show: Boolean)

    @Skip
    fun showAuthScreen()

    @Skip
    fun showLogOutDialog()

    @Skip
    fun showCommonPinPatternDialog()
}