package com.lobstr.stellar.vault.presentation.pin

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface PinView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTitle(@StringRes title: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setScreenStyle(style: Int)

    @StateStrategyType(SkipStrategy::class)
    fun resetPin()

    @StateStrategyType(SkipStrategy::class)
    fun showHomeScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showVaultAuthScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showBiometricSetUpScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(@StringRes message: Int)

    @StateStrategyType(SkipStrategy::class)
    fun finishScreenWithResult(resultCode: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog(show: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showBiometricDialog(show: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showAuthScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showLogOutDialog()

    @StateStrategyType(SkipStrategy::class)
    fun showCommonPinPatternDialog()
}