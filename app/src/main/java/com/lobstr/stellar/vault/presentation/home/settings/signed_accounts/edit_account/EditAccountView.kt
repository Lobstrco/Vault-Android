package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface EditAccountView : MvpView {
    fun copyToClipBoard(text: String)
}