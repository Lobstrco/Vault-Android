package com.lobstr.stellar.vault.presentation.home.transactions.operation

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import org.stellar.sdk.*

@InjectViewState
class OperationDetailsPresenter(private val mTransactionItem: TransactionItem, private val mPosition: Int) :
    BasePresenter<OperationDetailsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.transactions)
        val transaction: Transaction = Transaction.fromEnvelopeXdr(mTransactionItem.xdr)
        val operation: Operation = transaction.operations[mPosition]
        when (operation) {
            is PaymentOperation -> showPaymentOperation(operation)
            is CreateAccountOperation -> showCreateAccountOperation(operation)
            is PathPaymentOperation -> showPathPaymentOperation(operation)
            is ManageOfferOperation -> showManageOfferOperation(operation)
            is CreatePassiveOfferOperation -> showCreatePassiveOfferOperation(operation)
            is SetOptionsOperation -> showSetOptionsOperation(operation)
            is ChangeTrustOperation -> showChangeTrustOperation(operation)
            is AllowTrustOperation -> showAllowTrustOperation(operation)
            is AccountMergeOperation -> showAccountMergeOperation(operation)
            is InflationOperation -> showInflationOperation(operation)
            is ManageDataOperation -> showManageDataOperation(operation)
            is BumpSequenceOperation -> showBumpSequenceOperation(operation)
        }
    }

    private fun showPaymentOperation(operation: Operation) {

    }

    private fun showCreateAccountOperation(operation: CreateAccountOperation) {

    }

    private fun showPathPaymentOperation(operation: PathPaymentOperation) {

    }

    private fun showManageOfferOperation(operation: ManageOfferOperation) {

    }

    private fun showCreatePassiveOfferOperation(operation: CreatePassiveOfferOperation) {

    }

    private fun showSetOptionsOperation(operation: SetOptionsOperation) {

    }

    private fun showChangeTrustOperation(operation: ChangeTrustOperation) {

    }

    private fun showAllowTrustOperation(operation: AllowTrustOperation) {

    }

    private fun showAccountMergeOperation(operation: AccountMergeOperation) {

    }

    private fun showInflationOperation(operation: InflationOperation) {

    }

    private fun showManageDataOperation(operation: ManageDataOperation) {

    }

    private fun showBumpSequenceOperation(operation: BumpSequenceOperation) {

    }
}