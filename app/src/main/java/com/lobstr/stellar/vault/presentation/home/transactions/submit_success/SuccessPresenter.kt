package com.lobstr.stellar.vault.presentation.home.transactions.submit_success

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.transaction_success.TransactionSuccessInteractor
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS_NEED_ADDITIONAL_SIGNATURES
import com.lobstr.stellar.vault.presentation.util.VibrateType.TYPE_TWO
import com.lobstr.stellar.vault.presentation.util.manager.composeViewXdrUrl
import moxy.MvpPresenter
import javax.inject.Inject

class SuccessPresenter @Inject constructor(
    private val interactor: TransactionSuccessInteractor
) : MvpPresenter<SuccessView>() {

    lateinit var hash: String
    lateinit var envelopeXdr: String
    var status = SUCCESS

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.vibrate(TYPE_TWO)
        viewState.setDescription(
            when (status) {
                SUCCESS -> AppUtil.getString(R.string.transaction_confirmation_success_description)
                SUCCESS_NEED_ADDITIONAL_SIGNATURES -> AppUtil.getString(R.string.transaction_confirmation_success_signatures_required_description)
                else -> ""
            },
            when (status) {
                SUCCESS -> R.color.color_9b9b9b
                else -> R.color.color_d9534f
            }
        )
        viewState.showViewExplorer(status == SUCCESS)
        viewState.setupXdr(envelopeXdr)
        viewState.showXdrContainer(status != SUCCESS)
    }

    fun infoClicked() {
        viewState.showHelpScreen()
    }

    fun copySignedXdrClicked() {
        viewState.copyToClipBoard(envelopeXdr)
    }

    fun viewTransactionDetailsClicked() {
        viewState.showWebPage(composeViewXdrUrl(envelopeXdr))
    }

    fun viewExplorerClicked() {
        viewState.showWebPage(Constant.Explorer.TRANSACTION.plus(hash))
    }

    fun doneClicked() {
        viewState.finishScreen()
    }

    fun copyXdrClicked() {
        viewState.copyToClipBoard(envelopeXdr)
    }
}