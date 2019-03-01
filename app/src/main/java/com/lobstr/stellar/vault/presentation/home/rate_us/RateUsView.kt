package com.lobstr.stellar.vault.presentation.home.rate_us

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface RateUsView : MvpView {
    fun showStore(storeUrl: String)
}