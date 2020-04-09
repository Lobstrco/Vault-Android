package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface OperationListView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun initRecycledView()

    @AddToEndSingle
    fun setOperationsToList(operationList: MutableList<Int>)

    @Skip
    fun showOperationDetailsScreen(transactionItem: TransactionItem, position: Int)
}