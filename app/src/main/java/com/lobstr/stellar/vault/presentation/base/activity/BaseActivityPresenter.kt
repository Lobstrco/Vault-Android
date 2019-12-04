package com.lobstr.stellar.vault.presentation.base.activity

import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class BaseActivityPresenter : MvpPresenter<BaseActivityView>() {

    fun setActionBarTitle(title: String?) {
        viewState.setActionBarTitle(title)
    }

    fun changeHomeBtnVisibility(visible: Boolean) {
        viewState.changeActionBarIconVisibility(visible)
    }
}