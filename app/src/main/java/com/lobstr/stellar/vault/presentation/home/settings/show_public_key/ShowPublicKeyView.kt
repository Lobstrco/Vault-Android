package com.lobstr.stellar.vault.presentation.home.settings.show_public_key

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface ShowPublicKeyView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupPublicKey(publicKey: String)

    @StateStrategyType(SkipStrategy::class)
    fun copyToClipBoard(text: String)
}