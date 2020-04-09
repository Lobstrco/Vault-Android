package com.lobstr.stellar.vault.presentation.dialog.alert.progress

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface ProgressDialogView : MvpView {
    fun setTransparentBackground()
}