package com.lobstr.stellar.vault.presentation.home.settings.container

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class SettingsContainerPresenter : MvpPresenter<SettingsContainerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showSettingsFr()
    }
}