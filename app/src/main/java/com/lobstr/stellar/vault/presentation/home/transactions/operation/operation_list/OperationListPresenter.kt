package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import moxy.MvpPresenter

class OperationListPresenter(private val transactionItem: TransactionItem) : MvpPresenter<OperationListView>() {

    private var operationList: MutableList<Int> = mutableListOf()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.title_toolbar_transaction_details)
        viewState.initRecycledView()
        prepareOperationsList()
    }

    private fun prepareOperationsList() {

        // Prepare operations list for show it.
        operationList.clear()
        for (operation in transactionItem.transaction.operations) {
            val resId: Int = AppUtil.getTransactionOperationName(operation)
            if (resId != -1) {
                operationList.add(resId)
            }
        }
        viewState.setOperationsToList(operationList)
    }

    fun operationItemClicked(position: Int) {
        viewState.showOperationDetailsScreen(transactionItem, position)
    }
}