package com.lobstr.stellar.vault.presentation.vault_auth.signer_info

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface SignerInfoView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun showRecheckSingerScreen(userPublicKey: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupUserPublicKey(userPublicKey: String)

    @StateStrategyType(SkipStrategy::class)
    fun copyToClipBoard(text: String)
}