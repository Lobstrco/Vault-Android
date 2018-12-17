package com.lobstr.stellar.vault.presentation.auth

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class AuthPresenter : MvpPresenter<AuthView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showAuthFragment()
    }

    fun checkBackStackEntryCount(backStackEntryCount: Int) {
        if (backStackEntryCount > 1) {
            viewState.popBackStack()
        } else {
            viewState.finishAuthActivity()
        }
    }
}