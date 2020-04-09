package com.lobstr.stellar.vault.presentation.base.activity

import moxy.MvpPresenter

class BaseActivityPresenter : MvpPresenter<BaseActivityView>() {

    fun setActionBarTitle(title: String?) {
        viewState.setActionBarTitle(title)
    }

    fun changeHomeBtnVisibility(visible: Boolean) {
        viewState.changeActionBarIconVisibility(visible)
    }
}