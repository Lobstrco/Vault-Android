package com.lobstr.stellar.vault.presentation.vault_auth.signer_info

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class SignerInfoPresenter(private val userPublicKey: String) : MvpPresenter<SignerInfoView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupUserPublicKey(userPublicKey)
    }

    fun copyUserPiblicKey(userPublicKey: String?) {
        if (userPublicKey.isNullOrEmpty()) {
            return
        }

        viewState.copyToClipBoard(userPublicKey)
    }

    fun btnNextClicked() {
        viewState.showRecheckSingerScreen(userPublicKey)
    }
}