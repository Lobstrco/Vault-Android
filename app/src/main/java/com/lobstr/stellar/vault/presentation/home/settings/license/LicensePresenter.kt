package com.lobstr.stellar.vault.presentation.home.settings.license

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lobstr.stellar.vault.R

@InjectViewState
class LicensePresenter : MvpPresenter<LicenseView>() {

    companion object {
        private const val LICENSE_PATH = "file:///android_asset/license/license.htm"
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.title_toolbar_license)
        viewState.setupPagePath(LICENSE_PATH)
    }
}