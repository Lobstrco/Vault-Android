package com.lobstr.stellar.vault.presentation.home.rate_us

import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.domain.rate_us.RateUsInteractor
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.rate_us.RateUsModule
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Social.STORE_URL
import moxy.MvpPresenter
import javax.inject.Inject

class RateUsPresenter : MvpPresenter<RateUsView>() {

    @Inject
    lateinit var interactor: RateUsInteractor

    init {
        LVApplication.appComponent.plusRateUsComponent(RateUsModule()).inject(this)
    }

    fun rateUsClicked() {
        interactor.setRateUsState(Constant.RateUsState.RATED)
        viewState.showStore(STORE_URL.plus(BuildConfig.APPLICATION_ID))
    }

    fun dontAskAgainClicked() {
        interactor.setRateUsState(Constant.RateUsState.SKIPPED)
    }

    fun laterClicked() {
        interactor.setRateUsState(Constant.RateUsState.DEFERRED)
    }
}