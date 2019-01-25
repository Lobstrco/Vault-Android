package com.lobstr.stellar.vault.presentation.faq

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lobstr.stellar.vault.R

@InjectViewState
class FaqPresenter : MvpPresenter<FaqView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(R.string.title_toolbar_faq)
    }
}