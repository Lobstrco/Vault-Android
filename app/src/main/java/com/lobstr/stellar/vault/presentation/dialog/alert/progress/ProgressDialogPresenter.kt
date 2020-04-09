package com.lobstr.stellar.vault.presentation.dialog.alert.progress

import moxy.MvpPresenter

class ProgressDialogPresenter : MvpPresenter<ProgressDialogView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setTransparentBackground()
    }
}