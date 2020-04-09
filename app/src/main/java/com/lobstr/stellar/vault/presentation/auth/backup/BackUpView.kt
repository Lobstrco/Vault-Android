package com.lobstr.stellar.vault.presentation.auth.backup

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

@Skip
interface BackUpView : MvpView {

    fun showCreateMnemonicsScreen()

    fun showHelpScreen()
}