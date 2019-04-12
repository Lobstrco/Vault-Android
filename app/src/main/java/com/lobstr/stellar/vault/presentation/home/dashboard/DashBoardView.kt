package com.lobstr.stellar.vault.presentation.home.dashboard

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface DashboardView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPublicKey(publicKey: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSignersPublickKey(info: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSignersCount(count: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDashboardInfo(count: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showSignersScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(message: String)

    @StateStrategyType(SkipStrategy::class)
    fun navigateToTransactionList()

    @StateStrategyType(SkipStrategy::class)
    fun copyData(publicKey: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideSignersProgress()
}