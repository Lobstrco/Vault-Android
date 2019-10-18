package com.lobstr.stellar.vault.presentation.home.transactions.import_xdr

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem


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