package com.lobstr.stellar.vault.presentation.auth.enter_screen

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

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
}