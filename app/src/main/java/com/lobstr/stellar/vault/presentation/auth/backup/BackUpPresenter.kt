package com.lobstr.stellar.vault.presentation.auth.backup

import com.lobstr.stellar.vault.presentation.util.manager.SupportManager.ArticleID.RECOVERY_PHRASE
import moxy.MvpPresenter

class BackUpPresenter : MvpPresenter<BackUpView>() {

    fun nextClicked() {
        viewState.showCreateMnemonicsScreen()
    }

    fun infoClicked() {
        viewState.showHelpScreen(RECOVERY_PHRASE)
    }
}