package com.lobstr.stellar.vault.presentation.base.fragment

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface BaseFragmentView : MvpView {

    fun saveOptionsMenuVisibility(visible: Boolean)

    fun setOptionsMenuVisible(visible: Boolean)

    fun setActionBarTitle(title: String?)

    fun saveActionBarTitle(@StringRes titleRes: Int)

    fun saveActionBarTitle(title: String?)
}