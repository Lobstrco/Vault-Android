package com.lobstr.stellar.vault.presentation.auth

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface AuthView : MvpView {

    @AddToEndSingle
    fun setActionBarIconVisibility(visible: Boolean)

    @AddToEndSingle
    fun setupToolbarColor(
        @ColorRes color: Int
    )

    @AddToEndSingle
    fun setupToolbarUpArrow(
        @DrawableRes arrow: Int,
        @ColorRes color: Int
    )

    @AddToEndSingle
    fun setupToolbarTitleColor(
        @ColorRes color: Int
    )

    @Skip
    fun updateToolbar(
        @ColorRes toolbarColor: Int? = null,
        @DrawableRes upArrow: Int? = null,
        @ColorRes upArrowColor: Int? = null,
        @ColorRes titleColor: Int? = null
    )

    @Skip
    fun showAuthFragment()
}