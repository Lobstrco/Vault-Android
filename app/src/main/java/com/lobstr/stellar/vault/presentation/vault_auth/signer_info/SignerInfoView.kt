package com.lobstr.stellar.vault.presentation.vault_auth.signer_info

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface SignerInfoView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun checkExistenceLobstrApp()

    @StateStrategyType(SkipStrategy::class)
    fun showRecheckSingerScreen()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupUserPublicKey(userPublicKey: String?)

    @StateStrategyType(SkipStrategy::class)
    fun copyToClipBoard(text: String)

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showPublicKeyDialog(publicKey: String)

    @StateStrategyType(SkipStrategy::class)
    fun downloadLobstrApp()

    @StateStrategyType(SkipStrategy::class)
    fun openLobstrMultisigSetupScreen()

    @StateStrategyType(SkipStrategy::class)
    fun openLobstrApp()
}