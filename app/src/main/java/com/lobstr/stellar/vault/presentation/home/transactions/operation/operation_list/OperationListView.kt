package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface OperationListView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun initRecycledView()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setOperationsToList(operationList: MutableList<Int>)

    @StateStrategyType(SkipStrategy::class)
    fun showOperationDetailsScreen(transactionItem: TransactionItem, position: Int)
}