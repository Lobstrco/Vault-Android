package com.lobstr.stellar.vault.presentation.home.transactions.operation

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.*

@InjectViewState
class OperationDetailsPresenter(private val mTransactionItem: TransactionItem, private val mPosition: Int) :
    BasePresenter<OperationDetailsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.title_toolbar_transactions)
        val operation: Operation = mTransactionItem.transaction.operations[mPosition]
        when (operation) {
            is PaymentOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is CreateAccountOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is PathPaymentOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is ManageOfferOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is CreatePassiveOfferOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is SetOptionsOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is ChangeTrustOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is AllowTrustOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is AccountMergeOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is InflationOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is ManageDataOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is BumpSequenceOperation -> viewState.initRecycledView(operation.getFieldsMap())
        }
    }
}