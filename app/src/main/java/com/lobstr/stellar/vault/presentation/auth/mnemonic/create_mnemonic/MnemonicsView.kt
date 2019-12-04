package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface MnemonicsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setActionLayerVisibility(isVisible: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupMnemonics(mnemonicItems: List<MnemonicItem>)

    @StateStrategyType(SkipStrategy::class)
    fun showConfirmationScreen(mnemonics: ArrayList<MnemonicItem>)

    @StateStrategyType(SkipStrategy::class)
    fun copyToClipBoard(text: String)

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showDenyAccountCreationDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showAuthFr()
}