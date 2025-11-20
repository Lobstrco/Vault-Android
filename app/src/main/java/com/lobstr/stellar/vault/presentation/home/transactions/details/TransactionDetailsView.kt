package com.lobstr.stellar.vault.presentation.home.transactions.details

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.error.Error
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface TransactionDetailsView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun updateMenuItemsVisibility(hiddenItemIds: Set<Int>)

    @AddToEndSingle
    fun showOptionsMenu(show: Boolean)

    @AddToEndSingle
    fun initSignersRecycledView()

    @AddToEndSingle
    fun notifySignersAdapter(accounts: List<Account>)

    @AddToEndSingle
    fun showSignersProgress(show: Boolean)

    @AddToEndSingle
    fun showSignersContainer(show: Boolean)

    @AddToEndSingle
    fun showSignersCount(countSummary: String?, countToSubmit: String?)

    @AddToEndSingle
    fun initOperationList(title: Int, operations: List<Int>)

    @AddToEndSingle
    fun initOperationDetailsScreen(
        title: Int,
        operation: Operation,
        transactionSourceAccount: String
    )

    @Skip
    fun showOperationDetailsScreen(
        title: Int,
        operation: Operation,
        transactionSourceAccount: String
    )

    @Skip
    fun operationDetailsClicked(position: Int)

    @Skip
    fun scrollTo(x: Int, y: Int)

    @Skip
    fun showMessage(message: String?)

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @AddToEndSingle
    fun setActionBtnState(
        isConfirmVisible: Boolean,
        isDeclineVisible: Boolean,
        isConfirmEnabled: Boolean = true,
        isDeclineEnabled: Boolean = true
    )

    @AddToEndSingle
    fun showActionContainer(show: Boolean)

    @AddToEndSingle
    fun showWarningLabel(text: String, @ColorRes color: Int)

    @AddToEndSingle
    fun successDeclineTransaction(transactionItem: TransactionItem)

    @AddToEndSingle
    fun successConfirmTransaction(
        hash: String,
        envelopeXdr: String,
        transactionSuccessStatus: Byte,
        transactionItem: TransactionItem
    )

    @AddToEndSingle
    fun errorConfirmTransaction(error: Error)

    @AddToEndSingle
    fun setupTransactionInfo(fields: List<OperationField>)

    @AddToEndSingle
    fun showConfirmTransactionDialog(show: Boolean, message: String? = null)

    @Skip
    fun showDeclineTransactionDialog()

    @AddToEndSingle
    fun showSequenceNumberWarningDialog(show: Boolean)

    @Skip
    fun copyToClipBoard(text: String)

    @Skip
    fun showWebPage(url: String)

    @Skip
    fun showEditAccountDialog(address: String)

    @Skip
    fun showAssetInfoDialog(code: String, issuer: String?)

    @Skip
    fun showTangemScreen(tangemInfo: TangemInfo)

    @AddToEndSingle
    fun showMainContent(show: Boolean)

    @AddToEndSingle
    fun showPlaceholder(show: Boolean)

    @AddToEndSingle
    fun showEmptyState(show: Boolean, error: String? = null)
}