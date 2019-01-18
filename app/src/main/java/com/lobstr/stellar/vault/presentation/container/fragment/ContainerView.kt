package com.lobstr.stellar.vault.presentation.container.fragment

import androidx.fragment.app.Fragment
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem

@StateStrategyType(SkipStrategy::class)
interface ContainerView : MvpView {

    fun showAuthFr()

    fun showSignerInfoFr()

    fun showFingerprintSetUpFr()

    fun showDashBoardFr()

    fun showSettingsFr()

    fun showTransactionsFr()

    fun showTransactionDetails(target: Fragment?, transactionItem: TransactionItem)

    fun showMnemonicsFr()

    fun showSuccessFr(envelopeXdr: String, needAdditionalSignatures: Boolean)
}