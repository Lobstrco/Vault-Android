package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details

import androidx.annotation.StringRes
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface OperationDetailsView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun showContent(show: Boolean)

    @AddToEndSingle
    fun initRecycledView(fields: MutableList<OperationField>)

    @AddToEndSingle
    fun notifyAdapter()

    @Skip
    fun showEditAccountDialog(address: String)

    @Skip
    fun showAssetInfoDialog(code: String, issuer: String?)
}