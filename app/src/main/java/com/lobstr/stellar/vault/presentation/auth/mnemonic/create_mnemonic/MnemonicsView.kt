package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic

import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface MnemonicsView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(@StringRes titleRes: Int)

    @AddToEndSingle
    fun setActionLayerVisibility(isVisible: Boolean)

    @AddToEndSingle
    fun setupMnemonics(mnemonicItems: List<MnemonicItem>)

    @Skip
    fun showConfirmationScreen(mnemonics: ArrayList<MnemonicItem>)

    @Skip
    fun copyToClipBoard(text: String)

    @Skip
    fun showHelpScreen(articleId: Long)

    @Skip
    fun showDenyAccountCreationDialog()

    @AddToEndSingle
    fun showAuthFr()

    @Skip
    fun showScreenCaptureWarning()
}