package com.lobstr.stellar.vault.presentation.base.fragment

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class BaseFragmentPresenter : MvpPresenter<BaseFragmentView>() {

    private var toolbarTitle: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        // set default title
        setToolbarTitle(toolbarTitle)
    }

    fun setToolbarTitle() {
        viewState.setActionBarTitle(toolbarTitle)
    }

    fun setToolbarTitle(title: String?) {
        toolbarTitle = title
        viewState.setActionBarTitle(title)
    }
}