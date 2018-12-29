package com.lobstr.stellar.vault.presentation.base.fragment

import androidx.annotation.StringRes
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class BaseFragmentPresenter : MvpPresenter<BaseFragmentView>() {

    private var toolbarTitle: Int = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        // set default title
        setToolbarTitle(toolbarTitle)
    }

    fun setToolbarTitle() {
        viewState.setActionBarTitle(toolbarTitle)
    }

    fun setToolbarTitle(@StringRes titleRes: Int) {
        toolbarTitle = titleRes
        viewState.setActionBarTitle(titleRes)
    }
}