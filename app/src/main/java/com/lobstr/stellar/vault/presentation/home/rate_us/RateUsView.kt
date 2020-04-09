package com.lobstr.stellar.vault.presentation.home.rate_us

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

@Skip
interface RateUsView : MvpView {
    fun showStore(storeUrl: String)
}