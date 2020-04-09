package com.lobstr.stellar.vault.presentation.auth.restore_key

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.auth.restore_key.entities.RecoveryPhraseInfo
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface RecoverKeyFrView : MvpView {

    @AddToEndSingle
    fun showInputErrorIfNeeded(recoveryPhrasesInfo: List<RecoveryPhraseInfo>, phrases: String)

    @AddToEndSingle
    fun enableNextButton(enable: Boolean)

    @AddToEndSingle
    fun changeTextBackground(isError: Boolean)

    @Skip
    fun showPinScreen()

    @Skip
    fun showErrorMessage(@StringRes message: Int)

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @Skip
    fun showHelpScreen()
}