package com.lobstr.stellar.vault.presentation.vault_auth.signer_info

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface SignerInfoView : MvpView {

    @AddToEndSingle
    fun checkExistenceLobstrApp()

    @AddToEndSingle
    fun setupUserPublicKey(userPublicKey: String?)

    @Skip
    fun copyToClipBoard(text: String)

    @Skip
    fun showHelpScreen()

    @Skip
    fun showPublicKeyDialog(publicKey: String)

    @Skip
    fun downloadLobstrApp()

    @Skip
    fun openLobstrMultisigSetupScreen()

    @Skip
    fun openLobstrApp()

    @Skip
    fun showMessage(message: String?)

    @AddToEndSingle
    fun finishScreen()
}