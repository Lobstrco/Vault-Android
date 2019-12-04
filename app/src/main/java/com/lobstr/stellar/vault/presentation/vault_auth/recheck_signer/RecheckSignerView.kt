package com.lobstr.stellar.vault.presentation.vault_auth.recheck_signer

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface RecheckSignerView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupUserPublicKey(userPublicKey: String?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog(show: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(message: String?)

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(@StringRes messageRes: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showHomeScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showAuthScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showLogOutDialog()
}