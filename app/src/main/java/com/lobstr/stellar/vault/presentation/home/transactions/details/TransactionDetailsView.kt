package com.lobstr.stellar.vault.presentation.home.transactions.details

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem


interface TransactionDetailsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun initRecycledView()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setOperationsToList(operationList: MutableList<Int>)

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(message: String?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun dismissProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setActionBtnVisibility(isConfirmVisible: Boolean, isDenyVisible: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun successDenyTransaction(transactionItem: TransactionItem)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun successConfirmTransaction(envelopeXdr: String, needAdditionalSignatures: Boolean, transactionItem: TransactionItem)

    @StateStrategyType(SkipStrategy::class)
    fun showOperationDetailsScreen(transactionItem: TransactionItem, position: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showDenyTransactionDialog()
}