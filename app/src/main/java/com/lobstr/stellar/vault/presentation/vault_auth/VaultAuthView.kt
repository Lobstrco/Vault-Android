package com.lobstr.stellar.vault.presentation.vault_auth

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface VaultAuthView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun dismissProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSignerInfoFragment(userPublicKey: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRecheckSignerFragment(userPublicKey: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showHomeScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(message: String?)
}