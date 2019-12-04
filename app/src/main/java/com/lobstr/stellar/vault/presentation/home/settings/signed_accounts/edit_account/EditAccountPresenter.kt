package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account

import com.lobstr.stellar.vault.presentation.util.Constant
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class EditAccountPresenter(private val publicKey: String) : MvpPresenter<EditAccountView>() {

    fun copyPublicKeyClicked() {
        viewState.copyToClipBoard(publicKey)
    }

    fun openExplorerClicked() {
        viewState.openExplorer(Constant.Explorer.ACCOUNT.plus(publicKey))
    }
}