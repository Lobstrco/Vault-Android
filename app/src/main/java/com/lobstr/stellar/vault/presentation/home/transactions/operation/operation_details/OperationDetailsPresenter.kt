package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.*
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.CancelSellOfferOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.CreatePassiveSellOfferOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.ManageBuyOfferOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.SellOfferOperation
import com.lobstr.stellar.vault.presentation.util.AppUtil
import moxy.InjectViewState

@InjectViewState
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

        when (operation) {
            is PaymentOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is CreateAccountOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is PathPaymentStrictSendOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is PathPaymentStrictReceiveOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is SellOfferOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is CancelSellOfferOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is ManageBuyOfferOperation -> viewState.initRecycledView(operation.getFieldsMap())
            is CreatePassiveSellOfferOperation -> viewState.initRecycledView(operation.getFieldsMap())
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