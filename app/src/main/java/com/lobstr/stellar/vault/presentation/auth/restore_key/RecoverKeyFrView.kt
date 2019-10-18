package com.lobstr.stellar.vault.presentation.auth.restore_key

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lobstr.stellar.vault.presentation.auth.restore_key.entities.RecoveryPhraseInfo

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