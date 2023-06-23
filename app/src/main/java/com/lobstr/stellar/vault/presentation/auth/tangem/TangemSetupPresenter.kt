package com.lobstr.stellar.vault.presentation.auth.tangem

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.tangem.setup.TangemSetupInteractor
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Social.SIGNER_CARD_BUY
import com.lobstr.stellar.vault.presentation.util.Constant.Social.SIGNER_CARD_INFO
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager.ArticleID.SIGNING_WITH_VAULT_SIGNER_CARD
import moxy.MvpPresenter
import javax.inject.Inject


class TangemSetupPresenter @Inject constructor(private val interactor: TangemSetupInteractor) : MvpPresenter<TangemView>() {

    override fun attachView(view: TangemView?) {
        super.attachView(view)
        viewState.setupToolbar(R.color.color_e7ddf8)
    }

    override fun detachView(view: TangemView?) {
        viewState.setupToolbar(android.R.color.white)
        super.detachView(view)
    }

    fun scanClicked() {
        // Check NFC status and when NFC available - show Tangem.
        if (AppUtil.isNfcAvailable()) {
            viewState.showTangemScreen()
        } else {
            viewState.showNfcNotAvailable()
        }
    }

    fun learnMoreClicked() {
        viewState.showWebPage(SIGNER_CARD_INFO)
    }

    fun buyNowClicked() {
        viewState.showWebPage(SIGNER_CARD_BUY)
    }

    fun infoClicked() {
        viewState.showHelpScreen(SIGNING_WITH_VAULT_SIGNER_CARD)
    }

    fun handleTangemInfo(tangemInfo: TangemInfo?) {
        if (tangemInfo != null) {
            if (tangemInfo.cardStatusCode == Constant.TangemCardStatus.EMPTY) {
                viewState.showTangemCreateWalletScreen(TangemInfo().apply {
                    cardId = tangemInfo.cardId
                    curve = tangemInfo.curve
                })
            } else {
                interactor.savePublicKey(tangemInfo.accountId!!)
                interactor.saveTangemCardId(tangemInfo.cardId!!)
                viewState.showVaultAuthScreen()
            }
        }
    }
}