package com.lobstr.stellar.vault.presentation.container.activity

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface ContainerView : MvpView {

    @AddToEndSingle
    fun setupToolbar(
        @ColorRes toolbarColor: Int,
        @DrawableRes upArrow: Int,
        @ColorRes upArrowColor: Int,
        @ColorRes titleColor: Int
    )

    @Skip
    fun showContainerFr(vararg args: Pair<String, Any?>)
}