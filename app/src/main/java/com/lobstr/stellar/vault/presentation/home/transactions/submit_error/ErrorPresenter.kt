package com.lobstr.stellar.vault.presentation.home.transactions.submit_error

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.application.LVApplication

@InjectViewState
class ErrorPresenter(private val error: String) : MvpPresenter<ErrorView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.vibrate(longArrayOf(2000, 50, 50, 50))
        viewState.setupErrorInfo(
            handleErrorMessage(error)
        )
    }

    private fun handleErrorMessage(error: String): String {
        // add here specific errors
        val resValue = when (error) {
            "tx_bad_seq" -> R.string.text_tx_bad_seq
            else -> 0
        }

        return if (resValue == 0) error else LVApplication.sAppComponent.context.getString(resValue)
    }

    fun infoClicked() {
        viewState.showHelpScreen()
    }

    fun doneClicked() {
        viewState.finishScreen()
    }
}