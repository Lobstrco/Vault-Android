package com.lobstr.stellar.vault.presentation.home

import com.lobstr.stellar.vault.domain.home.HomeInteractor
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.home.HomeModule
import com.lobstr.stellar.vault.presentation.util.Constant
import moxy.MvpPresenter
import javax.inject.Inject

class HomeActivityPresenter : MvpPresenter<HomeActivityView>() {

    @Inject
    lateinit var interactor: HomeInteractor

    init {
        LVApplication.appComponent.plusHomeComponent(HomeModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar()

        interactor.checkFcmRegistration()

        viewState.initBottomNavigationView()

        viewState.setupViewPager()
    }

    /**
     * HomeActivity is responsible for check RateUs Dialog.
     */
    fun checkRateUsDialog() {
        if (interactor.getRateUsState() == Constant.RateUsState.RATED
            || interactor.getRateUsState() == Constant.RateUsState.SKIPPED
        ) {
            return
        }

        viewState.showRateUsDialog()
    }
}