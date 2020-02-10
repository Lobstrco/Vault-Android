package com.lobstr.stellar.vault.presentation.home.transactions.submit_error

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.util.AppUtil
import moxy.InjectViewState
import moxy.MvpPresenter

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
            "tx_bad_seq" -> R.string.text_tv_tx_bad_seq
            "tx_failed" -> R.string.text_tv_tx_failed
            "tx_too_early" -> R.string.text_tv_tx_too_early
            "tx_too_late" -> R.string.text_tv_tx_too_late
            "tx_missing_operation" -> R.string.text_tv_tx_missing_operation
            "tx_bad_auth" -> R.string.text_tv_tx_bad_auth
            "tx_insufficient_balance" -> R.string.text_tv_tx_insufficient_balance
            "tx_no_account" -> R.string.text_tv_tx_no_account
            "tx_insufficient_fee" -> R.string.text_tv_tx_insufficient_fee
            "tx_bad_auth_extra" -> R.string.text_tv_tx_bad_auth_extra
            "tx_internal_error" -> R.string.text_tv_tx_internal_error
            else -> 0
        }

        return if (resValue == 0) error else AppUtil.getString(resValue)
    }

    fun infoClicked() {
        viewState.showHelpScreen()
    }

    fun doneClicked() {
        viewState.finishScreen()
    }
}