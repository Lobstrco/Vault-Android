package com.lobstr.stellar.vault.presentation.home.dashboard.container

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface DashBoardContainerView : MvpView {
    @StateStrategyType(SkipStrategy::class)
    fun showDashBoardFr()
}