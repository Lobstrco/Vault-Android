package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account

import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface EditAccountView : MvpView {
    fun copyToClipBoard(text: String)
    fun openExplorer(url: String)
}