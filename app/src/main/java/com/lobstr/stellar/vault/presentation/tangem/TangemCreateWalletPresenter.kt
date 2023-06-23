package com.lobstr.stellar.vault.presentation.tangem

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager.ArticleID.SIGNING_WITH_VAULT_SIGNER_CARD
import moxy.MvpPresenter

class TangemCreateWalletPresenter(private var tangemInfo: TangemInfo?) :
    MvpPresenter<TangemCreateWalletView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar(
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_primary,
            android.R.color.black
        )

        if (tangemInfo == null) {
            tangemInfo = TangemInfo()
        }
    }

    fun infoClicked() {
        viewState.showHelpScreen(SIGNING_WITH_VAULT_SIGNER_CARD)
    }

    fun createWalletClicked() {
        if (!AppUtil.isNvsEnabled()) {
            viewState.showNfcCheckDialog()
        } else {
            if (!tangemInfo?.cardId.isNullOrEmpty()) {
                viewState.showTangemScreen(
                    TangemInfo().apply {
                        cardId = tangemInfo?.cardId
                        curve = tangemInfo?.curve
                    }
                )
            } else {
                viewState.showMessage("Error: CardId is empty!")
            }
        }
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.NFC_INFO_DIALOG -> {
                viewState.showNfcDeviceSettings()
            }
        }
    }

    fun handleTangemInfo(tangemInfo: TangemInfo?) {
        if (tangemInfo != null) {
            viewState.successfullyCompleteOperation(tangemInfo)
        }
    }
}