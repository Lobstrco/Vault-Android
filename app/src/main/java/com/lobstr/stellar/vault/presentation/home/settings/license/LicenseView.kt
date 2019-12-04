package com.lobstr.stellar.vault.presentation.home.settings.license

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface LicenseView : MvpView {

    fun setupToolbarTitle(@StringRes titleRes: Int)

    fun setupPagePath(path: String)
}