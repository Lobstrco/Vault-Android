package com.lobstr.stellar.vault.presentation.home.rate_us

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lobstr.stellar.vault.domain.rate_us.RateUsInteractor
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.rate_us.RateUsModule
import com.lobstr.stellar.vault.presentation.util.Constant
import javax.inject.Inject

@InjectViewState
class RateUsPresenter : MvpPresenter<RateUsView>() {

    @Inject
    lateinit var interactor: RateUsInteractor

    init {
        LVApplication.sAppComponent.plusRateUsComponent(RateUsModule()).inject(this)
    }

    fun rateUsClicked() {
        interactor.setRateUsState(Constant.RateUsState.RATED)
        viewState.showStore(Constant.Social.STORE_URL)
    }

    fun dontAskAgainClicked() {
        interactor.setRateUsState(Constant.RateUsState.SKIPPED)
    }

    fun laterClicked() {
        interactor.setRateUsState(Constant.RateUsState.DEFERRED)
    }
}