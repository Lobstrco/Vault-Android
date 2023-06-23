package com.lobstr.stellar.vault.presentation.home.settings.license

import com.lobstr.stellar.vault.R
import moxy.MvpPresenter

class LicensePresenter : MvpPresenter<LicenseView>() {

    companion object {
        private const val LICENSE_PATH = "file:///android_asset/license/license.htm"
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.license_title)
        viewState.setupPagePath(LICENSE_PATH)
    }
}