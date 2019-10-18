package com.lobstr.stellar.vault.presentation.container.activity

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.DASHBOARD
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.ERROR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.IMPORT_XDR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.MNEMONICS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SETTINGS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SIGNED_ACCOUNTS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SUCCESS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTIONS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTION_DETAILS

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

            SUCCESS -> viewState.showSuccessFr(envelopeXdr!!, needAdditionalSignatures!!)

            ERROR -> viewState.showErrorFr(errorMessage!!)

            SIGNED_ACCOUNTS -> viewState.showSignedAccountsFr()
        }
    }
}