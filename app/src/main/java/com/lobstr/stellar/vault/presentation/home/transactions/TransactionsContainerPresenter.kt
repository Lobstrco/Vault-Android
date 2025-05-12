package com.lobstr.stellar.vault.presentation.home.transactions

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BasePresenter
import javax.inject.Inject

class TransactionsContainerPresenter @Inject constructor() :
    BasePresenter<TransactionsContainerView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(R.string.toolbar_transactions_title)
        viewState.initViewPager()
    }
}