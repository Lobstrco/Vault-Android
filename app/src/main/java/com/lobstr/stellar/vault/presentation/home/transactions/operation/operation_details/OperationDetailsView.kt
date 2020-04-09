package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface OperationDetailsView : MvpView {

    fun setupToolbarTitle(@StringRes titleRes: Int)

    fun initRecycledView(map: Map<String, String?>)
}