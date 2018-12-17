package com.lobstr.stellar.vault.presentation.home.transactions.container

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface TransactionsContainerView : MvpView {
    fun showTransactionsFr()
}