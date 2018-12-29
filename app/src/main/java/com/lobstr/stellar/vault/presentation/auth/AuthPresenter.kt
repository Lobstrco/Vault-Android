package com.lobstr.stellar.vault.presentation.auth

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lobstr.stellar.vault.R

@InjectViewState
class AuthPresenter : MvpPresenter<AuthView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar(
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_ff3a6c99
        )

        viewState.showAuthFragment()
    }
}