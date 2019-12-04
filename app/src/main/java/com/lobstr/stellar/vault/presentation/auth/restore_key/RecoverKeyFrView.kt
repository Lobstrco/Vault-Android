package com.lobstr.stellar.vault.presentation.auth.restore_key

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.auth.restore_key.entities.RecoveryPhraseInfo
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface RecoverKeyFrView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showInputErrorIfNeeded(recoveryPhrasesInfo: List<RecoveryPhraseInfo>, phrases: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun enableNextButton(enable: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun changeTextBackground(isError: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showPinScreen()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(@StringRes message: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog(show: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showHelpScreen()
}