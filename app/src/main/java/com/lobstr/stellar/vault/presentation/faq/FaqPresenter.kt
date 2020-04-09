package com.lobstr.stellar.vault.presentation.faq

import com.lobstr.stellar.vault.R
import moxy.MvpPresenter

class FaqPresenter : MvpPresenter<FaqView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(R.string.title_toolbar_faq)
    }
}