package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface OperationListView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun initRecycledView(operations: List<Int>)

    @Skip
    fun showOperationDetailsScreen(position: Int)
}