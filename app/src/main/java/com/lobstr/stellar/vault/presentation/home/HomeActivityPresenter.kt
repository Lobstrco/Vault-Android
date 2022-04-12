package com.lobstr.stellar.vault.presentation.home

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.home.HomeInteractor
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.util.Constant
import moxy.MvpPresenter
import javax.inject.Inject

class HomeActivityPresenter @Inject constructor(private var interactor: HomeInteractor) :
    MvpPresenter<HomeActivityView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar(R.drawable.ic_arrow_back, android.R.color.white)
        initData()
    }

    private fun initData() {
        interactor.checkFcmRegistration()

        viewState.initBottomNavigationView()
        viewState.setupViewPager()
    }

    override fun attachView(view: HomeActivityView?) {
        super.attachView(view)
        // Check Rate Us behavior state.
        checkRateUsDialog()
    }

    /**
     * HomeActivity is responsible for check RateUs Dialog.
     */
    fun checkRateUsDialog() {
        if (LVApplication.checkRateUsDialogState == Constant.RateUsSessionState.CHECK) {
            LVApplication.checkRateUsDialogState = Constant.RateUsSessionState.CHECKED

            if (interactor.getRateUsState() == Constant.RateUsState.RATED
                || interactor.getRateUsState() == Constant.RateUsState.SKIPPED
            ) {
                return
            }

            viewState.suggestRateUsDialog()
        }
    }

    fun accountWasChanged() {
        initData()
    }
}