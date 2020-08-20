package com.lobstr.stellar.vault.presentation.vault_auth

import com.lobstr.stellar.vault.R
import moxy.MvpPresenter

class VaultAuthPresenter : MvpPresenter<VaultAuthView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar(
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_primary
        )

        viewState.showAuthTokenFragment()
    }
}