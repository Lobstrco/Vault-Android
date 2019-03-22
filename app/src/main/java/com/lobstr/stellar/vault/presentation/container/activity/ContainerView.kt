package com.lobstr.stellar.vault.presentation.container.activity

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem

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
}