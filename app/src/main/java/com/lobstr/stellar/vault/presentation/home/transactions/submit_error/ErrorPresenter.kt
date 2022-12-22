package com.lobstr.stellar.vault.presentation.home.transactions.submit_error

import com.lobstr.stellar.vault.domain.transaction_error.TransactionErrorInteractor
import com.lobstr.stellar.vault.presentation.entities.error.Error
import com.lobstr.stellar.vault.presentation.util.AppUtil
import moxy.MvpPresenter
import javax.inject.Inject

class ErrorPresenter @Inject constructor(private val interactor: TransactionErrorInteractor) :
    MvpPresenter<ErrorView>() {

    lateinit var error: Error

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.vibrate(longArrayOf(2000, 50, 50, 50))
        viewState.setupXdr(error.xdr)
        viewState.setupErrorInfo(
            error.shortDescription ?: error.description,
            !error.shortDescription.isNullOrEmpty()
        )
    }

    fun infoClicked() {
        viewState.showHelpScreen(interactor.getUserPublicKey())
    }

    fun viewErrorDetailsClicked() {
        viewState.showErrorDetails(error.description)
    }

    fun copySignedXdrClicked() {
        viewState.copyToClipBoard(error.xdr)
    }

    fun viewTransactionDetailsClicked() {
        viewState.showWebPage(AppUtil.composeLaboratoryUrl(error.xdr))
    }

    fun doneClicked() {
        viewState.finishScreen()
    }
}