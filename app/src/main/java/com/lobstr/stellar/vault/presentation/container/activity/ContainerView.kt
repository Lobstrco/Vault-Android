package com.lobstr.stellar.vault.presentation.container.activity

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem

@StateStrategyType(SkipStrategy::class)
interface ContainerView : MvpView {

    fun showDashBoardFr()

    fun showSettingsFr()

    fun showTransactionsFr()

    fun showTransactionDetails(transactionItem: TransactionItem)

    fun showMnemonicsFr()

    fun showSuccessFr(envelopeXdr: String, needAdditionalSignatures: Boolean)
}