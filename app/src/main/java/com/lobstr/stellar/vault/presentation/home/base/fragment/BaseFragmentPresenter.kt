package com.lobstr.stellar.vault.presentation.home.base.fragment

import androidx.annotation.StringRes
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class BaseFragmentPresenter : MvpPresenter<BaseFragmentView>() {

    private var mToolbarTitle: Int = 0

    fun setToolbarTitle() {
        if (mToolbarTitle == 0) {
            return
        }

        viewState.setActionBarTitle(mToolbarTitle)
    }

    fun setToolbarTitle(@StringRes titleRes: Int) {
        mToolbarTitle = titleRes
        viewState.setActionBarTitle(titleRes)
    }
}