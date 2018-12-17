package com.lobstr.stellar.vault.presentation.dialog.alert.progress

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface ProgressDialogView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setTransparentBackground()
}