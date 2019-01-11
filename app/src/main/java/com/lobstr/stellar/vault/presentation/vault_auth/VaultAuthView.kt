package com.lobstr.stellar.vault.presentation.vault_auth

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface VaultAuthView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbar(@ColorRes toolbarColor: Int, @DrawableRes upArrow: Int, @ColorRes upArrowColor: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun dismissProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSignerInfoFragment()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showHomeScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(message: String?)
}