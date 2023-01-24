package com.lobstr.stellar.vault.presentation.home

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.home.HomeInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.util.Constant
import moxy.MvpPresenter
import javax.inject.Inject

class HomeActivityPresenter @Inject constructor(
    private var interactor: HomeInteractor,
    private val eventProviderModule: EventProviderModule
) :
    MvpPresenter<HomeActivityView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar(R.drawable.ic_arrow_back, android.R.color.white)
        initData()
    }

    private fun initData() {
        interactor.checkFcmRegistration()

        // Check the current user's notifications state and show permission dialog.
        if (interactor.isNotificationsEnabled()) {
            viewState.checkPostNotificationsPermission()
        }

        viewState.initBottomNavigationView()
        viewState.setupViewPager()
    }

    override fun attachView(view: HomeActivityView?) {
        super.attachView(view)
        // Check Rate Us behavior state.
        checkRateUsDialog()
    }

    fun postNotificationsPermissionChanged(granted: Boolean) {
        interactor.setNotificationsEnabled(granted)
        // Notify other places for the relevant actions.
        eventProviderModule.updateEventSubject.onNext(Update(if (granted) Update.Type.POST_NOTIFICATIONS_GRANTED else Update.Type.POST_NOTIFICATIONS_DENIED))
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