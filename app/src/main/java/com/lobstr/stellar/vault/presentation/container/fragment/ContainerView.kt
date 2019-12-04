package com.lobstr.stellar.vault.presentation.container.fragment

import androidx.fragment.app.Fragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface ContainerView : MvpView {

    fun showAuthFr()

    fun showSignerInfoFr()

    fun showBiometricSetUpFr()

    fun showDashBoardFr()

    fun showSettingsFr()

    fun showTransactionsFr()

    fun showTransactionDetails(target: Fragment?, transactionItem: TransactionItem)

    fun showImportXdrFr()

    fun showMnemonicsFr()

    fun showSuccessFr(envelopeXdr: String, needAdditionalSignatures: Boolean)

    fun showErrorFr(errorMessage: String)

    fun showSignedAccountsFr()
}