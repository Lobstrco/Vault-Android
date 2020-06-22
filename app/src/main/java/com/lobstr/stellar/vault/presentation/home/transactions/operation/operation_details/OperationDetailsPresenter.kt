package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.vault.presentation.util.AppUtil

class OperationDetailsPresenter(private val mTransactionItem: TransactionItem, private val mPosition: Int) :
    BasePresenter<OperationDetailsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        // Check case, when operations list is empty.
        if (mTransactionItem.transaction.operations.isNullOrEmpty()) {
            viewState.setupToolbarTitle(R.string.title_toolbar_transaction_details)
            return
        }

        val operation: Operation = mTransactionItem.transaction.operations[mPosition]

        viewState.setupToolbarTitle(AppUtil.getTransactionOperationName(operation))

        viewState.initRecycledView(operation.getFields())
    }
}