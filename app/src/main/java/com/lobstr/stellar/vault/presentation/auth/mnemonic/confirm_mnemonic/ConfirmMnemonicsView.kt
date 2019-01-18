package com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface ConfirmMnemonicsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupMnemonicsToSelect(mnemonics: List<String>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupMnemonicsToConfirm(mnemonics: List<String>)

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(message: String)

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(@StringRes message: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showPinScreen()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun dismissProgressDialog()

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()
}