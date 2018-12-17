package com.lobstr.stellar.vault.presentation.auth.enter_screen

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class AuthFrPresenter : MvpPresenter<AuthFrView>() {

    fun newClicked() {
        viewState.showCreateMnemonicsScreen()
    }

    fun restoreClicked() {
        viewState.showRestoreScreen()
    }
}