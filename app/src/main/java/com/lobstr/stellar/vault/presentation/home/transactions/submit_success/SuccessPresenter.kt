package com.lobstr.stellar.vault.presentation.home.transactions.submit_success

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.presentation.BasePresenter

@InjectViewState
class SuccessPresenter(
    private val envelopeXdr: String,
    private val needAdditionalSignatures: Boolean
) : BasePresenter<SuccessView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAdditionalSignaturesInfoEnabled(
            needAdditionalSignatures
        )
    }

    fun backClicked() {
        viewState.finishScreen()
    }

    fun copyXdrClicked() {
        viewState.copyToClipBoard(envelopeXdr)
    }
}