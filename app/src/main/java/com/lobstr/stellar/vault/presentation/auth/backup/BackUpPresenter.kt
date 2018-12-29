package com.lobstr.stellar.vault.presentation.auth.backup

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class BackUpPresenter : MvpPresenter<BackUpView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setNextBtnEnabled(false)
    }

    fun helpClicked() {
        viewState.showHelpScreen()
    }

    fun nextClicked() {
        viewState.showCreateMnemonicsScreen()
    }

    fun confirmClicked(isChecked: Boolean) {
        viewState.setNextBtnEnabled(isChecked)
    }
}