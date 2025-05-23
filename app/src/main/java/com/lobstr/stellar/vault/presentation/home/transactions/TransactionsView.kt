package com.lobstr.stellar.vault.presentation.home.transactions

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface TransactionsView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun setEmptyState(title: String)

    @AddToEndSingle
    fun showFab(show: Boolean)

    @AddToEndSingle
    fun initRecycledView()

    @Skip
    fun showTransactionDetails(transactionItem: TransactionItem)

    @Skip
    fun showErrorMessage(message: String)

    @AddToEndSingle
    fun showTransactionList(items: List<TransactionItem>, needShowProgress: Boolean)

    @Skip
    fun scrollListToPosition(position: Int)

    @AddToEndSingle
    fun showOptionsMenu(show: Boolean)

    @AddToEndSingle
    fun showPullToRefresh(show: Boolean)

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @AddToEndSingle
    fun showEmptyState(show: Boolean)

    @Skip
    fun showImportXdrScreen()

    @Skip
    fun showClearTransactionsDialog(
        tag: String,
        message: String,
        positiveBtnText: String,
        neutralBtnText: String?,
        negativeBtnText: String
    )

    @Skip
    fun showDeclineTransactionDialog()
}