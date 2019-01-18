package com.lobstr.stellar.vault.presentation.auth.restore_key

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lobstr.stellar.vault.presentation.auth.restore_key.entities.PhraseErrorInfo

@StateStrategyType(SkipStrategy::class)
interface RecoveryKeyFrView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showInputErrorIfNeeded(incorrectPhrases: List<PhraseErrorInfo>, phrases: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun enableNextButton(enable: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun changeTextBackground(isError: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showPinScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(@StringRes message: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun dismissProgressDialog()

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()
}