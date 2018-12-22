package com.lobstr.stellar.vault.presentation.home.base.fragment

import androidx.annotation.StringRes
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class BaseFragmentPresenter : MvpPresenter<BaseFragmentView>() {

    private var toolbarTitle: Int = 0

    fun setToolbarTitle() {
        if (toolbarTitle == 0) {
            return
        }

        viewState.setActionBarTitle(toolbarTitle)
    }

    fun setToolbarTitle(@StringRes titleRes: Int) {
        toolbarTitle = titleRes
        viewState.setActionBarTitle(titleRes)
    }
}