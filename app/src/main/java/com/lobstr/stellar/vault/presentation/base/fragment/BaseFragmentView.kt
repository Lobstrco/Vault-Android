package com.lobstr.stellar.vault.presentation.base.fragment

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface BaseFragmentView : MvpView {

    fun setActionBarTitle(title: String?)

    fun saveActionBarTitle(@StringRes titleRes: Int)

    fun saveActionBarTitle(title: String?)
}