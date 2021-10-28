package com.lobstr.stellar.vault.presentation.home.transactions.submit_error

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.transaction_error.TransactionErrorInteractor
import com.lobstr.stellar.vault.presentation.util.AppUtil
import moxy.MvpPresenter
import javax.inject.Inject

class ErrorPresenter @Inject constructor(private val interactor: TransactionErrorInteractor) : MvpPresenter<ErrorView>() {

    lateinit var error: String
    lateinit var envelopeXdr: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.vibrate(longArrayOf(2000, 50, 50, 50))
        viewState.setupXdr(envelopeXdr)
        viewState.setupErrorInfo(
            handleErrorMessage(error)
        )
    }

    private fun handleErrorMessage(error: String): String {
        // Add here specific errors.
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

            "op_inner" -> R.string.text_tv_op_inner
            "op_bad_auth" -> R.string.text_tv_op_bad_auth
            "op_no_account" -> R.string.text_tv_op_no_account
            "op_not_supported" -> R.string.text_tv_op_not_supported
            "op_too_many_subentries" -> R.string.text_tv_op_too_many_subentries
            "op_exceeded_work_limit" -> R.string.text_tv_op_exceeded_work_limit
            "op_too_many_sponsoring" -> R.string.text_tv_op_too_many_sponsoring
            "op_underfunded" -> R.string.text_tv_op_underfunded
            else -> 0
        }

        return if (resValue == 0) error else AppUtil.getString(resValue)
    }

    fun infoClicked() {
        viewState.showHelpScreen(interactor.getUserPublicKey())
    }

    fun copySignedXdrClicked() {
        viewState.copyToClipBoard(envelopeXdr)
    }

    fun viewTransactionDetailsClicked() {
        viewState.showWebPage(AppUtil.composeLaboratoryUrl(envelopeXdr))
    }

    fun doneClicked() {
        viewState.finishScreen()
    }
}