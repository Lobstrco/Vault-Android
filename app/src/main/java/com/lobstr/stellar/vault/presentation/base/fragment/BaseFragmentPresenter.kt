package com.lobstr.stellar.vault.presentation.base.fragment

import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class BaseFragmentPresenter : MvpPresenter<BaseFragmentView>() {

    // Set by default options menu visible.
    private var optionsMenuVisibility: Boolean = true

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

    fun saveOptionsMenuVisibilityCalled(visible: Boolean) {
        optionsMenuVisibility = visible
        viewState.setOptionsMenuVisible(optionsMenuVisibility)
    }

    fun checkOptionsMenuVisibility(){
        viewState.setOptionsMenuVisible(optionsMenuVisibility)
    }
}