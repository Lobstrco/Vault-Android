package com.lobstr.stellar.vault.presentation.home.transactions.submit_success

import com.lobstr.stellar.vault.domain.transaction_success.TransactionSuccessInteractor
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.transaction_success.TransactionSuccessModule
import moxy.MvpPresenter
import javax.inject.Inject

class SuccessPresenter(
    private val envelopeXdr: String,
    private val needAdditionalSignatures: Boolean
) : MvpPresenter<SuccessView>() {

    @Inject
    lateinit var interactor: TransactionSuccessInteractor

    init {
        LVApplication.appComponent.plusTransactionSuccessComponent(TransactionSuccessModule())
            .inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
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