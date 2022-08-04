package com.lobstr.stellar.vault.presentation.home.account_name.add

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle


interface AddAccountNameView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun setSaveEnabled(enabled: Boolean)

    @AddToEndSingle
    fun showAddressFormError(show: Boolean)

    @AddToEndSingle
    fun showNameFormError(show: Boolean)

    @AddToEndSingle
    fun closeScreen()
}