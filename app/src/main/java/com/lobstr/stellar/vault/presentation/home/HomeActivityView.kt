package com.lobstr.stellar.vault.presentation.home

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface HomeActivityView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun initBottomNavigationView()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolBarTitle(@StringRes title: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showAuthScreen()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupViewPager()

    @StateStrategyType(SkipStrategy::class)
    fun resetBackStack()

    @StateStrategyType(SkipStrategy::class)
    fun setSelectedBottomNavigationItem(@IdRes itemId: Int)
}