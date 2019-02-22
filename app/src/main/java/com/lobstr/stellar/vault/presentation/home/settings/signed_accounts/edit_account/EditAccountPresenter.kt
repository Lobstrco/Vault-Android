package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class EditAccountPresenter(private val publicKey: String) : MvpPresenter<EditAccountView>() {

    fun copyPublicKeyClicked() {
        viewState.copyToClipBoard(publicKey)
    }
}