package com.lobstr.stellar.vault.presentation.dialog.alert.progress

import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class ProgressDialogPresenter : MvpPresenter<ProgressDialogView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setTransparentBackground()
    }
}