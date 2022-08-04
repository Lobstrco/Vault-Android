package com.lobstr.stellar.vault.presentation.container.fragment

import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.ADD_ACCOUNT_NAME
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.AUTH
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.CONFIG
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.DASHBOARD
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.ERROR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.IMPORT_XDR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.MNEMONICS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SETTINGS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SIGNED_ACCOUNTS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SIGNER_INFO
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SUCCESS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTIONS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTION_DETAILS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.VAULT_AUTH
import moxy.MvpPresenter

class ContainerPresenter(
    private val targetFr: Int,
    private val transactionItem: TransactionItem?,
    private val envelopeXdr: String?,
    private val transactionSuccessStatus: Byte?,
    private val errorMessage: String?,
    private val config: Int
) : MvpPresenter<ContainerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        navigateTo()
    }

    private fun navigateTo() {
        when (targetFr) {
            AUTH -> viewState.showAuthFr()

            VAULT_AUTH -> viewState.showVaultAuthFr()

            SIGNER_INFO -> viewState.showSignerInfoFr()

            DASHBOARD -> viewState.showDashBoardFr()

            TRANSACTIONS -> viewState.showTransactionsFr()

            SETTINGS -> viewState.showSettingsFr()

            TRANSACTION_DETAILS -> viewState.showTransactionDetails(transactionItem!!)

            IMPORT_XDR -> viewState.showImportXdrFr()

            MNEMONICS -> viewState.showMnemonicsFr()

            SUCCESS -> viewState.showSuccessFr(envelopeXdr!!, transactionSuccessStatus!!)

            ERROR -> viewState.showErrorFr(errorMessage!!, envelopeXdr!!)

            SIGNED_ACCOUNTS -> viewState.showSignedAccountsFr()

            CONFIG -> viewState.showConfigFr(config)

            ADD_ACCOUNT_NAME -> viewState.showAddAccountNameFr()
        }
    }
}