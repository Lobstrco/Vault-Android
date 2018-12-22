package com.lobstr.stellar.vault.presentation.home

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class HomeActivityPresenter : MvpPresenter<HomeActivityView>() {

    init {
        // TODO
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.initBottomNavigationView()

        viewState.setupViewPager()
    }
}