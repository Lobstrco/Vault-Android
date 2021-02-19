package com.lobstr.stellar.vault.presentation.home

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface HomeActivityView : MvpView {

    @AddToEndSingle
    fun setupToolbar(@DrawableRes upArrow: Int,
                     @ColorRes upArrowColor: Int,)

    @AddToEndSingle
    fun initBottomNavigationView()

    @AddToEndSingle
    fun setupViewPager()

    @Skip
    fun resetBackStack()

    @Skip
    fun setSelectedBottomNavigationItem(@IdRes itemId: Int)

    @Skip
    fun checkRateUsDialog()

    @Skip
    fun suggestRateUsDialog()
}