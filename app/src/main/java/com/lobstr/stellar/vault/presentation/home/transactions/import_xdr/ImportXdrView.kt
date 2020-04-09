package com.lobstr.stellar.vault.presentation.home.transactions.import_xdr

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface ImportXdrView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @Skip
    fun showMessage(message: String?)

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @Skip
    fun showTransactionDetails(transactionItem: TransactionItem)

    @AddToEndSingle
    fun setSubmitEnabled(enabled: Boolean)

    @AddToEndSingle
    fun showFormError(show: Boolean, error: String?)
}