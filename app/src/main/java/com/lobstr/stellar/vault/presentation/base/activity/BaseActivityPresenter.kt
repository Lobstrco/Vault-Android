package com.lobstr.stellar.vault.presentation.base.activity

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class BaseActivityPresenter : MvpPresenter<BaseActivityView>() {

    fun setActionBarTitle(title: String?) {
        viewState.setActionBarTitle(title)
    }

    fun changeHomeBtnVisibility(visible: Boolean) {
        viewState.changeActionBarIconVisibility(visible)
    }
}