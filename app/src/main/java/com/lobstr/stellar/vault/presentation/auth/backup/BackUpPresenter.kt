package com.lobstr.stellar.vault.presentation.auth.backup

import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class BackUpPresenter : MvpPresenter<BackUpView>() {

    fun nextClicked() {
        viewState.showCreateMnemonicsScreen()
    }

    fun infoClicked() {
        viewState.showHelpScreen()
    }
}