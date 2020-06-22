package com.lobstr.stellar.vault.presentation.auth.backup

import com.lobstr.stellar.vault.presentation.util.Constant
import moxy.MvpPresenter

class BackUpPresenter : MvpPresenter<BackUpView>() {

    fun nextClicked() {
        viewState.showCreateMnemonicsScreen()
    }

    fun infoClicked() {
        viewState.showHelpScreen(Constant.Support.RECOVERY_PHRASE)
    }
}