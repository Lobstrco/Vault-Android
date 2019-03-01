package com.lobstr.stellar.vault.presentation.home.settings.license

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface LicenseView : MvpView {

    fun setupToolbarTitle(@StringRes titleRes: Int)

    fun setupPagePath(path: String)
}