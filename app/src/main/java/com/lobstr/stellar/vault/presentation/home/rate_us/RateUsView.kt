package com.lobstr.stellar.vault.presentation.home.rate_us

import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface RateUsView : MvpView {
    fun showStore(storeUrl: String)
}