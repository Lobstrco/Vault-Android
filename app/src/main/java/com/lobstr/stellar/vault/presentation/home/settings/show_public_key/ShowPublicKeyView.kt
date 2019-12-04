package com.lobstr.stellar.vault.presentation.home.settings.show_public_key

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface ShowPublicKeyView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupPublicKey(publicKey: String)

    @StateStrategyType(SkipStrategy::class)
    fun copyToClipBoard(text: String)
}