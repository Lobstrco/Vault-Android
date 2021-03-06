package com.lobstr.stellar.vault.presentation.container.activity

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
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
import moxy.MvpPresenter

class ContainerPresenter(
    private val targetFr: Int,
    private val transactionItem: TransactionItem?,
    private val envelopeXdr: String?,
    private val transactionConfirmationSuccessStatus: Byte?,
    private val errorMessage: String?,
    private val config: Int
) : MvpPresenter<ContainerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar(
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_primary,
            android.R.color.black
        )

        navigateTo()
    }

    private fun navigateTo() {
        when (targetFr) {
            DASHBOARD -> viewState.showDashBoardFr()

            TRANSACTIONS -> viewState.showTransactionsFr()

            SETTINGS -> viewState.showSettingsFr()

            TRANSACTION_DETAILS -> viewState.showTransactionDetails(transactionItem!!)

            IMPORT_XDR -> viewState.showImportXdrFr()

            MNEMONICS -> viewState.showMnemonicsFr()

            SUCCESS -> viewState.showSuccessFr(envelopeXdr!!, transactionConfirmationSuccessStatus!!)

            ERROR -> viewState.showErrorFr(errorMessage!!)

            SIGNED_ACCOUNTS -> viewState.showSignedAccountsFr()

            SIGNER_INFO -> viewState.showSignerInfoFr()

            CONFIG -> viewState.showConfigFr(config)
        }
    }
}