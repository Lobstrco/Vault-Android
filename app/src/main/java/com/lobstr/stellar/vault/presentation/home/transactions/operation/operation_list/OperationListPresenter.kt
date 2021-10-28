package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_list

import com.lobstr.stellar.tsmapper.presentation.util.Constant.TransactionType.AUTH_CHALLENGE
import com.lobstr.stellar.tsmapper.presentation.util.TsUtil
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpPresenter

class OperationListPresenter(private val transactionItem: TransactionItem) :
    MvpPresenter<OperationListView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(
            when (transactionItem.transaction.transactionType) {
                AUTH_CHALLENGE -> R.string.text_transaction_challenge
                else -> R.string.title_toolbar_transaction_details
            }
        )
        viewState.initRecycledView(mutableListOf<Int>().apply {
            // Prepare operations list for show it.
            for (operation in transactionItem.transaction.operations) {
                val resId: Int =
                    TsUtil.getTransactionOperationName(operation)
                if (resId != -1) {
                    add(resId)
                }
            }
        })
    }

    fun operationItemClicked(position: Int) {
        viewState.showOperationDetailsScreen(transactionItem, position)
    }
}