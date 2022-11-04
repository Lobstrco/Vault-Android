package com.lobstr.stellar.vault.presentation.base.fragment

import moxy.MvpPresenter

class BaseFragmentPresenter : MvpPresenter<BaseFragmentView>() {

    private var toolbarTitle: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        // Set default title.
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