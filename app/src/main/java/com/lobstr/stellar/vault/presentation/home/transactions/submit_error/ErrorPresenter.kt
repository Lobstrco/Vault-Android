package com.lobstr.stellar.vault.presentation.home.transactions.submit_error

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class ErrorPresenter(private val error: String) : MvpPresenter<ErrorView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupErrorInfo(error)
    }

    fun doneClicked() {
        viewState.finishScreen()
    }
}