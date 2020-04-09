package com.lobstr.stellar.vault.presentation.auth.backup

import moxy.MvpPresenter

class BackUpPresenter : MvpPresenter<BackUpView>() {

    fun nextClicked() {
        viewState.showCreateMnemonicsScreen()
    }

    fun infoClicked() {
        viewState.showHelpScreen()
    }
}