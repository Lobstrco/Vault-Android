package com.lobstr.stellar.vault.presentation.auth

import com.lobstr.stellar.vault.R
import moxy.MvpPresenter

class AuthPresenter : MvpPresenter<AuthView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar(
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_primary
        )

        viewState.showAuthFragment()
    }
}