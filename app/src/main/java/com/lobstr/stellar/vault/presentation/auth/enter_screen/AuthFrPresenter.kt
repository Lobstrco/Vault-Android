package com.lobstr.stellar.vault.presentation.auth.enter_screen

import moxy.MvpPresenter

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

    fun tangemClicked() {
        viewState.showTangemSetupScreen()
    }
}