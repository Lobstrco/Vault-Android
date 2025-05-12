package com.lobstr.stellar.vault.presentation.base.fragment

import moxy.MvpPresenter

class BaseFragmentPresenter(private val showCommonToolbar: Boolean) :
    MvpPresenter<BaseFragmentView>() {

    private var toolbarTitle: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        // Set default title.
        setToolbarTitle(toolbarTitle)
    }

    fun setToolbarTitle() {
        if (showCommonToolbar) {
            viewState.setActionBarTitle(toolbarTitle)
        }
    }

    fun setToolbarTitle(title: String?) {
        if (showCommonToolbar) {
            toolbarTitle = title
            viewState.setActionBarTitle(title)
        }
    }
}