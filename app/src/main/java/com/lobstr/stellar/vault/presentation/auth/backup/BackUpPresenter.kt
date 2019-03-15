package com.lobstr.stellar.vault.presentation.auth.backup

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class BackUpPresenter : MvpPresenter<BackUpView>() {

    fun nextClicked() {
        viewState.showCreateMnemonicsScreen()
    }

    fun infoClicked() {
        viewState.showHelpScreen()
    }
}