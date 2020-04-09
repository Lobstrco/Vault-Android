package com.lobstr.stellar.vault.presentation.home

import androidx.annotation.IdRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface HomeActivityView : MvpView {

    @AddToEndSingle
    fun setupToolbar()

    @AddToEndSingle
    fun initBottomNavigationView()

    @AddToEndSingle
    fun showAuthScreen()

    @AddToEndSingle
    fun setupViewPager()

    @Skip
    fun resetBackStack()

    @Skip
    fun setSelectedBottomNavigationItem(@IdRes itemId: Int)

    @Skip
    fun checkRateUsDialog()

    @Skip
    fun showRateUsDialog()
}