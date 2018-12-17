package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface CreateMnemonicsView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupMnemonics(mnemonics: List<String>)

    @StateStrategyType(SkipStrategy::class)
    fun showConfirmationScreen(mnemonics: CharArray)
}