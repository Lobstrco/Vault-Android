package com.lobstr.stellar.vault.presentation.home.transactions.details

import androidx.annotation.StringRes
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface TransactionDetailsView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun initSignersRecycledView()

    @AddToEndSingle
    fun notifySignersAdapter(accounts: List<Account>)

    @AddToEndSingle
    fun showSignersProgress(show: Boolean)

    @AddToEndSingle
    fun showSignersContainer(show: Boolean)

    @AddToEndSingle
    fun showSignersCount(count: String?)

    @Skip
    fun showOperationList(transactionItem: TransactionItem)

    @Skip
    fun showMessage(message: String?)

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @AddToEndSingle
    fun setActionBtnVisibility(isConfirmVisible: Boolean, isDenyVisible: Boolean)

    @AddToEndSingle
    fun showActionContainer(show: Boolean)

    @AddToEndSingle
    fun setTransactionValid(valid: Boolean)

    @AddToEndSingle
    fun successDenyTransaction(transactionItem: TransactionItem)

    @AddToEndSingle
    fun successConfirmTransaction(
        envelopeXdr: String,
        transactionSuccessStatus: Byte,
        transactionItem: TransactionItem
    )

    @AddToEndSingle
    fun errorConfirmTransaction(errorMessage: String, envelopeXdr: String)

    @AddToEndSingle
    fun showOperationDetailsScreen(transactionItem: TransactionItem, position: Int)

    @AddToEndSingle
    fun setupTransactionInfo(fields: List<OperationField>)

    @Skip
    fun showConfirmTransactionDialog()

    @Skip
    fun showDenyTransactionDialog()

    @Skip
    fun copyToClipBoard(text: String)

    @Skip
    fun showWebPage(url: String)

    @Skip
    fun showEditAccountDialog(address: String)

    @Skip
    fun showTangemScreen(tangemInfo: TangemInfo)
}