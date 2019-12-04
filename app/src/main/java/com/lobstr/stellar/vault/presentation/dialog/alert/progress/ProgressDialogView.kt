package com.lobstr.stellar.vault.presentation.dialog.alert.progress

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface ProgressDialogView : MvpView {
    fun setTransparentBackground()
}