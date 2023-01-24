package com.lobstr.stellar.vault.presentation.home.transactions.submit_success

import com.lobstr.stellar.vault.domain.transaction_success.TransactionSuccessInteractor
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS_NEED_ADDITIONAL_SIGNATURES
import com.lobstr.stellar.vault.presentation.util.VibratorUtil.VibrateType.TYPE_TWO
import moxy.MvpPresenter
import javax.inject.Inject

class SuccessPresenter @Inject constructor(
    private val interactor: TransactionSuccessInteractor
) : MvpPresenter<SuccessView>() {

    lateinit var envelopeXdr: String
    var status = SUCCESS

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.vibrate(TYPE_TWO)
        viewState.setupXdr(envelopeXdr)
        viewState.setAdditionalSignaturesInfoEnabled(
            status == SUCCESS_NEED_ADDITIONAL_SIGNATURES
        )
        viewState.showXdrContainer(status != SUCCESS)
    }

    fun infoClicked() {
        viewState.showHelpScreen()
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

    fun copyXdrClicked() {
        viewState.copyToClipBoard(envelopeXdr)
    }
}