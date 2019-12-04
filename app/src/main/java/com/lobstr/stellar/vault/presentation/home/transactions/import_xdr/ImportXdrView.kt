package com.lobstr.stellar.vault.presentation.home.transactions.import_xdr

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface ImportXdrView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(message: String?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog(show: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showTransactionDetails(transactionItem: TransactionItem)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setSubmitEnabled(enabled: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFormError(show: Boolean, error: String?)
}