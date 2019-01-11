package com.lobstr.stellar.vault.presentation.auth.touch_id

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface FingerprintSetUpView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun showVaultAuthScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showFingerprintInfoDialog(@StringRes titleRes: Int, @StringRes messageRes: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showBiometricDialog()
}