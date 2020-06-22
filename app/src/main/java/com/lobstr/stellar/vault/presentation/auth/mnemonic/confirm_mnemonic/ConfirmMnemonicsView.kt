package com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface ConfirmMnemonicsView : MvpView {

    @AddToEndSingle
    fun setupMnemonicsToSelect(mnemonics: List<MnemonicItem>)

    @AddToEndSingle
    fun setupMnemonicsToConfirm(mnemonics: List<MnemonicItem>)

    @Skip
    fun showMessage(message: String)

    @Skip
    fun showMessage(@StringRes message: Int)

    @Skip
    fun showPinScreen()

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @Skip
    fun showHelpScreen(articleId: Long)

    @AddToEndSingle
    fun setActionButtonEnabled(enabled: Boolean)
}