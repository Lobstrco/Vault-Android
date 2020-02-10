package com.lobstr.stellar.vault.presentation.home.transactions.details

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface TransactionDetailsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun initSignersRecycledView()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun notifySignersAdapter(accounts: List<Account>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSignersProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSignersContainer(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSignersCount(count: String?)

    @StateStrategyType(SkipStrategy::class)
    fun showOperationList(transactionItem: TransactionItem)

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(message: String?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setActionBtnVisibility(isConfirmVisible: Boolean, isDenyVisible: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setTransactionValid(valid: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun successDenyTransaction(transactionItem: TransactionItem)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun successConfirmTransaction(
        envelopeXdr: String,
        needAdditionalSignatures: Boolean,
        transactionItem: TransactionItem
    )

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun errorConfirmTransaction(errorMessage: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showOperationDetailsScreen(transactionItem: TransactionItem, position: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupAdditionalInfo(map: Map<String, String>)

    @StateStrategyType(SkipStrategy::class)
    fun showConfirmTransactionDialog()

    @StateStrategyType(SkipStrategy::class)
    fun showDenyTransactionDialog()

    @StateStrategyType(SkipStrategy::class)
    fun copyToClipBoard(text: String)
}