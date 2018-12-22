package com.lobstr.stellar.vault.presentation.home.settings

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.settings.SettingsInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dager.module.settings.SettingsModule
import javax.inject.Inject

@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>() {

    @Inject
    lateinit var interactor: SettingsInteractor

    init {
        LVApplication.sAppComponent.plusSettingsComponent(SettingsModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(R.string.settings)
        viewState.setupAccountData(
            interactor.getUserPublicKey(),
            interactor.getSignedAccount()
        )
    }

    fun infoClicked() {
        viewState.showInfoFr()
    }

    fun logOutClicked() {
        interactor.clearUserData()
        viewState.showAuthScreen()
    }

    fun copyUserPublicKey(userPublicKey: String?) {
        if (userPublicKey.isNullOrEmpty()) {
            return
        }

        viewState.copyToClipBoard(userPublicKey)
    }
}