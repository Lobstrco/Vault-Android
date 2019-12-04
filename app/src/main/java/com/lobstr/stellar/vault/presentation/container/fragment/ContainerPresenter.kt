package com.lobstr.stellar.vault.presentation.container.fragment

import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.AUTH
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.BIOMETRIC_SET_UP
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
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class ContainerPresenter(
    private val targetFr: Int,
    private val transactionItem: TransactionItem?,
    private val envelopeXdr: String?,
    private val needAdditionalSignatures: Boolean?,
    private val errorMessage: String?
) : MvpPresenter<ContainerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        navigateTo()
    }

    private fun navigateTo() {
        when (targetFr) {
            AUTH -> viewState.showAuthFr()

            SIGNER_INFO -> viewState.showSignerInfoFr()

            BIOMETRIC_SET_UP -> viewState.showBiometricSetUpFr()

            DASHBOARD -> viewState.showDashBoardFr()

            TRANSACTIONS -> viewState.showTransactionsFr()

            SETTINGS -> viewState.showSettingsFr()

            TRANSACTION_DETAILS -> viewState.showTransactionDetails(null, transactionItem!!)

            IMPORT_XDR -> viewState.showImportXdrFr()

            MNEMONICS -> viewState.showMnemonicsFr()

            SUCCESS -> viewState.showSuccessFr(envelopeXdr!!, needAdditionalSignatures!!)

            ERROR -> viewState.showErrorFr(errorMessage!!)

            SIGNED_ACCOUNTS -> viewState.showSignedAccountsFr()
        }
    }
}