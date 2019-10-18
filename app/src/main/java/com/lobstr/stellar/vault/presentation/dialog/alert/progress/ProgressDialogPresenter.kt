package com.lobstr.stellar.vault.presentation.dialog.alert.progress

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class ProgressDialogPresenter : MvpPresenter<ProgressDialogView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setTransparentBackground()
    }
}