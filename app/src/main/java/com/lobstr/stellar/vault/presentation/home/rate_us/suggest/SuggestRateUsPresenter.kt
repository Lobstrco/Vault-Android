package com.lobstr.stellar.vault.presentation.home.rate_us.suggest

import moxy.MvpPresenter

class SuggestRateUsPresenter : MvpPresenter<SuggestRateUsView>() {

    fun rateClicked() {
        viewState.showRateUsDialog()
    }

    fun cancelClicked() {
        viewState.showFeedbackDialog()
    }
}