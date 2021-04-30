package com.lobstr.stellar.vault.presentation.home.rate_us.suggest

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

@Skip
interface SuggestRateUsView : MvpView {
    fun showRateUsDialog()
    fun showFeedbackDialog()
}