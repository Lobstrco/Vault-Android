package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem


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