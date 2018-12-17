package com.lobstr.stellar.vault.presentation.home

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dager.module.fcm.FcmInternalModule
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import javax.inject.Inject

@InjectViewState
class HomeActivityPresenter : MvpPresenter<HomeActivityView>() {

    @Inject
    lateinit var mFcmHelper: FcmHelper

    init {
        // FIXME Sample for FCM Integration
        LVApplication.sAppComponent.plusFcmInternalComponent(FcmInternalModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

//        mFcmHelper.checkIfFcmRegisteredSuccessfully()

        viewState.initBottomNavigationView()

        viewState.setupViewPager()
    }
}