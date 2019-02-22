package com.lobstr.stellar.vault.presentation.home.transactions.submit_success

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class SuccessPresenter(
    private val envelopeXdr: String,
    private val needAdditionalSignatures: Boolean
) : MvpPresenter<SuccessView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setMovementMethods()
        viewState.vibrate(longArrayOf(1500, 175, 0, 0))
        viewState.setupXdr(envelopeXdr)
        viewState.setAdditionalSignaturesInfoEnabled(
            needAdditionalSignatures
        )
    }

    fun doneClicked() {
        viewState.finishScreen()
    }

    fun copyXdrClicked() {
        viewState.copyToClipBoard(envelopeXdr)
    }
}