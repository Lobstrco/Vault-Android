package com.lobstr.stellar.vault.presentation.home.transactions

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BasePresenter

@InjectViewState
class TransactionsPresenter : BasePresenter<TransactionsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(R.string.transactions)
    }
}