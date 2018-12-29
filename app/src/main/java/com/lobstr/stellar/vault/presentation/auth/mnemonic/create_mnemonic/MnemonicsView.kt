package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface MnemonicsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setActionBtnVisibility(isVisible: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupMnemonics(mnemonics: List<String>)

    @StateStrategyType(SkipStrategy::class)
    fun showConfirmationScreen(mnemonics: CharArray)

    @StateStrategyType(SkipStrategy::class)
    fun copyToClipBoard(text: String)
}