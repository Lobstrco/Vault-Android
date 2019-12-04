package com.lobstr.stellar.vault.presentation.home

import androidx.annotation.IdRes
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface HomeActivityView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbar()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun initBottomNavigationView()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showAuthScreen()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupViewPager()

    @StateStrategyType(SkipStrategy::class)
    fun resetBackStack()

    @StateStrategyType(SkipStrategy::class)
    fun setSelectedBottomNavigationItem(@IdRes itemId: Int)

    @StateStrategyType(SkipStrategy::class)
    fun checkRateUsDialog()

    @StateStrategyType(SkipStrategy::class)
    fun showRateUsDialog()
}