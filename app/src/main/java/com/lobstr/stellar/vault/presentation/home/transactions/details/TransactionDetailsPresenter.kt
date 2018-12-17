package com.lobstr.stellar.vault.presentation.home.transactions.details

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BasePresenter

@InjectViewState
class TransactionDetailsPresenter : BasePresenter<TransactionDetailsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(R.string.transaction_details)
    }
}