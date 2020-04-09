package com.lobstr.stellar.vault.presentation.home.settings.show_public_key

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface ShowPublicKeyView : MvpView {

    @AddToEndSingle
    fun setupPublicKey(publicKey: String)

    @Skip
    fun copyToClipBoard(text: String)
}