package com.lobstr.stellar.vault.presentation.home.dashboard

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface DashboardView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDashboardInfo(count: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideProgress()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(message: String)
}