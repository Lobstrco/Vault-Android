package com.lobstr.stellar.vault.presentation.home.settings

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface SettingsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupSettingsData(
        userPublicKey: String?,
        buildVersion: String,
        isBiometricSupported: Boolean
    )

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupSignersCount(
        signersCount: String
    )

    @StateStrategyType(SkipStrategy::class)
    fun copyToClipBoard(text: String)

    @StateStrategyType(SkipStrategy::class)
    fun showSuccessMessage(@StringRes message: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showInfoFr()

    @StateStrategyType(SkipStrategy::class)
    fun showAuthScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showSignersScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showMnemonicsScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showChangePinScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setTouchIdChecked(checked: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showFingerprintInfoDialog(@StringRes titleRes: Int, @StringRes messageRes: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showPublicKeyDialog(publicKey: String)
}