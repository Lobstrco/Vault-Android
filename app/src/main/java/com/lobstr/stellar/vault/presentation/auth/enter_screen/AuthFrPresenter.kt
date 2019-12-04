package com.lobstr.stellar.vault.presentation.auth.enter_screen

import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class AuthFrPresenter : MvpPresenter<AuthFrView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setMovementMethods()
    }

    fun newClicked() {
        viewState.showBackUpScreen()
    }

    fun restoreClicked() {
        viewState.showRestoreScreen()
    }

    fun helpClicked() {
        viewState.showHelpScreen()
    }
}