package com.lobstr.stellar.vault.presentation.container.activity

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface ContainerView : MvpView {

    @AddToEndSingle
    fun setupToolbar(
        @ColorRes toolbarColor: Int,
        @DrawableRes upArrow: Int,
        @ColorRes upArrowColor: Int,
        @ColorRes titleColor: Int
    )

    @Skip
    fun showSignerInfoFr()

    @Skip
    fun showDashBoardFr()

    @Skip
    fun showSettingsFr()

    @Skip
    fun showTransactionsFr()

    @Skip
    fun showTransactionDetails(transactionItem: TransactionItem)

    @Skip
    fun showImportXdrFr()

    @Skip
    fun showMnemonicsFr()

    @Skip
    fun showSuccessFr(envelopeXdr: String, transactionSuccessStatus: Byte)

    @Skip
    fun showErrorFr(errorMessage: String)

    @Skip
    fun showSignedAccountsFr()

    @Skip
    fun showConfigFr(config: Int)
}