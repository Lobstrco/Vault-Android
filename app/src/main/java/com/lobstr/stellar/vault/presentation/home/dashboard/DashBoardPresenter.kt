package com.lobstr.stellar.vault.presentation.home.dashboard

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BasePresenter

@InjectViewState
class DashBoardPresenter : BasePresenter<DashBoardView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(R.string.dashboard)
    }
}