package com.lobstr.stellar.vault.presentation.vault_auth.recheck_signer

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


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
}