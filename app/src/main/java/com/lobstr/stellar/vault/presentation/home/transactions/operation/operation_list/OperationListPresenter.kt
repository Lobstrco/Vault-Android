package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list

import androidx.annotation.StringRes
import moxy.MvpPresenter

class OperationListPresenter(
    @StringRes private val title: Int,
    private val operations: List<Int>
) :
    MvpPresenter<OperationListView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(title)
        viewState.initRecycledView(operations)
    }

    fun operationItemClicked(position: Int) {
        viewState.showOperationDetailsScreen(position)
    }
}