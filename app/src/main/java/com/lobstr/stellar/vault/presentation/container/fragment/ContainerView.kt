package com.lobstr.stellar.vault.presentation.container.fragment

import com.lobstr.stellar.vault.presentation.entities.error.Error
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

@Skip
interface ContainerView : MvpView {

    fun showAuthFr()

    fun showVaultAuthFr()

    fun showSignerInfoFr()

    fun showDashBoardFr()

    fun showSettingsFr()

    fun showTransactionsFr()

    fun showTransactionDetails(transactionItem: TransactionItem)

    fun showImportXdrFr()

    fun showMnemonicsFr()

    fun showSuccessFr(envelopeXdr: String, transactionSuccessStatus: Byte)

    fun showErrorFr(error: Error)

    fun showSignedAccountsFr()

    fun showConfigFr(config: Int)

    fun showAddAccountNameFr()
}