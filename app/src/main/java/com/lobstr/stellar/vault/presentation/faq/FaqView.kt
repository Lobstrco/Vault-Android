package com.lobstr.stellar.vault.presentation.faq

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface FaqView : MvpView {
    fun setupToolbarTitle(@StringRes titleRes: Int)
}