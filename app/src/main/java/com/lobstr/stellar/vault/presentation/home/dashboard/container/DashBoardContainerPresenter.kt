package com.lobstr.stellar.vault.presentation.home.dashboard.container

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class DashBoardContainerPresenter : MvpPresenter<DashBoardContainerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.showDashBoardFr()
    }
}