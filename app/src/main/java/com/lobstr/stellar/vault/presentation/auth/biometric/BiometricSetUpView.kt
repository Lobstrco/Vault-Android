package com.lobstr.stellar.vault.presentation.auth.biometric

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

@Skip
interface BiometricSetUpView : MvpView {

    fun showVaultAuthScreen()

    fun showBiometricInfoDialog(@StringRes titleRes: Int, @StringRes messageRes: Int)

    fun showBiometricDialog(show: Boolean)
}