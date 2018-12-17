package com.lobstr.stellar.vault.presentation.home.base.activity

import androidx.annotation.StringRes
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class BaseActivityPresenter : MvpPresenter<BaseActivityView>() {

    fun setActionBarTitle(@StringRes titleRes: Int) {
        viewState.setActionBarTitle(titleRes)
    }

    fun changeHomeBtnVisibility(visible: Boolean) {
        viewState.changeActionBarIconVisibility(visible)
    }
}