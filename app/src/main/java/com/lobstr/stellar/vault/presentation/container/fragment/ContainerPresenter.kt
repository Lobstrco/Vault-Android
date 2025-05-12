package com.lobstr.stellar.vault.presentation.container.fragment

import android.os.Bundle
import com.lobstr.stellar.vault.presentation.util.Constant
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
import com.lobstr.stellar.vault.presentation.util.parcelable
import moxy.MvpPresenter

class ContainerPresenter(
    private val arguments: Bundle
) : MvpPresenter<ContainerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        navigateTo()
    }

    private fun navigateTo() {
        when (arguments.getInt(Constant.Bundle.BUNDLE_NAVIGATION_FR)) {
            AUTH -> viewState.showAuthFr()

            VAULT_AUTH -> viewState.showVaultAuthFr()

            SIGNER_INFO -> viewState.showSignerInfoFr()

            DASHBOARD -> viewState.showDashBoardFr()

            TRANSACTIONS -> viewState.showTransactionsContainerFr()

            SETTINGS -> viewState.showSettingsFr()

            TRANSACTION_DETAILS -> viewState.showTransactionDetails(arguments.parcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM)!!)

            IMPORT_XDR -> viewState.showImportXdrFr()

            MNEMONICS -> viewState.showMnemonicsFr()

            SUCCESS -> viewState.showSuccessFr(arguments.getString(Constant.Bundle.BUNDLE_ENVELOPE_XDR)!!, arguments.getByte(
                Constant.Bundle.BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS,
                Constant.TransactionConfirmationSuccessStatus.SUCCESS
            )!!)

            ERROR -> viewState.showErrorFr(arguments.parcelable(Constant.Bundle.BUNDLE_ERROR)!!)

            SIGNED_ACCOUNTS -> viewState.showSignedAccountsFr()

            CONFIG -> viewState.showConfigFr(arguments.getInt(Constant.Bundle.BUNDLE_CONFIG,
                Constant.Util.UNDEFINED_VALUE
            ))

            ADD_ACCOUNT_NAME -> viewState.showAddAccountNameFr()
        }
    }
}