package com.lobstr.stellar.vault.presentation.home.transactions

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem


interface TransactionsView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun initRecycledView()

    @StateStrategyType(SkipStrategy::class)
    fun showTransactionDetails(transactionItem: TransactionItem)

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(message: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTransactionList(items: MutableList<TransactionItem>, needShowProgress: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun scrollListToPosition(position: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showOptionsMenu(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPullToRefresh(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyState()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideEmptyState()

    @StateStrategyType(SkipStrategy::class)
    fun showImportXdrScreen()

    @StateStrategyType(SkipStrategy::class)
    fun checkRateUsDialog()

    @StateStrategyType(SkipStrategy::class)
    fun showClearInvalidTrDialog()
}