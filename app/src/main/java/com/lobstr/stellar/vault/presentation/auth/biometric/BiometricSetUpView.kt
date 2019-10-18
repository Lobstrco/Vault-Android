package com.lobstr.stellar.vault.presentation.auth.biometric

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface BiometricSetUpView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun showVaultAuthScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showBiometricInfoDialog(@StringRes titleRes: Int, @StringRes messageRes: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showBiometricDialog(show: Boolean)
}