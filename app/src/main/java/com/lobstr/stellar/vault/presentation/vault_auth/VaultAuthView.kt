package com.lobstr.stellar.vault.presentation.vault_auth

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface VaultAuthView : MvpView {

    @AddToEndSingle
    fun setupToolbar(
        @ColorRes toolbarColor: Int,
        @DrawableRes upArrow: Int,
        @ColorRes upArrowColor: Int
    )

    @Skip
    fun showAuthTokenFragment()
}