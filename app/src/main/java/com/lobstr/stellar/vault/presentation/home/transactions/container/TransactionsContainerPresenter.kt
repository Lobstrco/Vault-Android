package com.lobstr.stellar.vault.presentation.home.transactions.container

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class TransactionsContainerPresenter : MvpPresenter<TransactionsContainerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showTransactionsFr()
    }
}