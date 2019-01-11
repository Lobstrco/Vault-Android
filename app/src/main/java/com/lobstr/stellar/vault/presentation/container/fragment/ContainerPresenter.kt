package com.lobstr.stellar.vault.presentation.container.fragment

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.AUTH
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.DASHBOARD
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.FINGERPRINT_SET_UP
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.MNEMONICS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SETTINGS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SIGNER_INFO
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTIONS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTION_DETAILS

@InjectViewState
class ContainerPresenter(
    private val targetFr: Int,
    private val transactionItem: TransactionItem?
) : MvpPresenter<ContainerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        navigateTo()
    }

    private fun navigateTo() {
        when (targetFr) {
            AUTH -> viewState.showAuthFr()

            SIGNER_INFO -> viewState.showSignerInfoFr()

            FINGERPRINT_SET_UP -> viewState.showFingerprintSetUpFr()

            DASHBOARD -> viewState.showDashBoardFr()

            TRANSACTIONS -> viewState.showTransactionsFr()

            SETTINGS -> viewState.showSettingsFr()

            TRANSACTION_DETAILS -> {
                if (transactionItem != null)
                    viewState.showTransactionDetails(null, transactionItem)
            }

            MNEMONICS -> viewState.showMnemonicsFr()
        }
    }
}