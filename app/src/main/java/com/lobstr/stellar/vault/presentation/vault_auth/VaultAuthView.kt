package com.lobstr.stellar.vault.presentation.vault_auth

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface VaultAuthView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbar(@ColorRes toolbarColor: Int, @DrawableRes upArrow: Int, @ColorRes upArrowColor: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setBtnRetryVisibility(visible: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSignerInfoFragment()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showHomeScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(message: String?)
}