package com.lobstr.stellar.vault.presentation.home.base.fragment

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface BaseFragmentView : MvpView {

    fun setActionBarTitle(@StringRes titleRes: Int)

    fun saveActionBarTitle(@StringRes titleRes: Int)
}