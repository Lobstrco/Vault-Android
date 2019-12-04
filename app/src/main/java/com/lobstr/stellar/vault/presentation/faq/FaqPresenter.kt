package com.lobstr.stellar.vault.presentation.faq

import com.lobstr.stellar.vault.R
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class FaqPresenter : MvpPresenter<FaqView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(R.string.title_toolbar_faq)
    }
}