package com.lobstr.stellar.vault.presentation.home.settings.show_public_key

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class ShowPublicKeyPresenter(private val publicKey: String) : MvpPresenter<ShowPublicKeyView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupPublicKey(publicKey)
    }

    fun copyPublicKeyClicked() {
        viewState.copyToClipBoard(publicKey)
    }
}