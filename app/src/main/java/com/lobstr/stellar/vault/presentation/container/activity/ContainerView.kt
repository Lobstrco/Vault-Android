package com.lobstr.stellar.vault.presentation.container.activity

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface ContainerView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbar(@ColorRes toolbarColor: Int, @DrawableRes upArrow: Int, @ColorRes upArrowColor: Int, @ColorRes titleColor: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showDashBoardFr()

    @StateStrategyType(SkipStrategy::class)
    fun showSettingsFr()

    @StateStrategyType(SkipStrategy::class)
    fun showTransactionsFr()

    @StateStrategyType(SkipStrategy::class)
    fun showTransactionDetails(transactionItem: TransactionItem)

    @StateStrategyType(SkipStrategy::class)
    fun showImportXdrFr()

    @StateStrategyType(SkipStrategy::class)
    fun showMnemonicsFr()

    @StateStrategyType(SkipStrategy::class)
    fun showSuccessFr(envelopeXdr: String, needAdditionalSignatures: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showErrorFr(errorMessage: String)

    @StateStrategyType(SkipStrategy::class)
    fun showSignedAccountsFr()
}