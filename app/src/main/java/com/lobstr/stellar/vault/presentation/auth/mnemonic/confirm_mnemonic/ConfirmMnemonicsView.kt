package com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType


interface ConfirmMnemonicsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupMnemonicsToSelect(mnemonics: List<MnemonicItem>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupMnemonicsToConfirm(mnemonics: List<MnemonicItem>)

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(message: String)

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(@StringRes message: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showPinScreen()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog(show: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setActionButtonEnabled(enabled: Boolean)
}