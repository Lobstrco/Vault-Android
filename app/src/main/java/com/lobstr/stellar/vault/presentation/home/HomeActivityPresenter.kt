package com.lobstr.stellar.vault.presentation.home

import com.lobstr.stellar.vault.domain.home.HomeInteractor
import com.lobstr.stellar.vault.presentation.util.Constant
import moxy.MvpPresenter

class HomeActivityPresenter(private var interactor: HomeInteractor) : MvpPresenter<HomeActivityView>() {

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