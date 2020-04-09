package com.lobstr.stellar.vault.presentation.vault_auth.recheck_signer

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface RecheckSignerView : MvpView {

    @AddToEndSingle
    fun setupUserPublicKey(userPublicKey: String?)

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @Skip
    fun showMessage(message: String?)

    @Skip
    fun showMessage(@StringRes messageRes: Int)

    @AddToEndSingle
    fun showHomeScreen()

    @Skip
    fun showAuthScreen()

    @Skip
    fun showLogOutDialog()
}