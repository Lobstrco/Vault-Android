package com.lobstr.stellar.vault.presentation.pin

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface PinView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun attachIndicatorDots()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTitle(@StringRes title: Int)

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
    fun showProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDescriptionMessage(@StringRes message: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showBiometricDialog()
}