package com.lobstr.stellar.vault.presentation.pin

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


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
    fun showFingerprintSetUpScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(@StringRes message: Int)

    @StateStrategyType(SkipStrategy::class)
    fun finishScreenWithResult(resultCode: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog(show: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showBiometricDialog()

    @StateStrategyType(SkipStrategy::class)
    fun showAuthScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showLogOutDialog()

    @StateStrategyType(SkipStrategy::class)
    fun showCommonPinPatternDialog()
}