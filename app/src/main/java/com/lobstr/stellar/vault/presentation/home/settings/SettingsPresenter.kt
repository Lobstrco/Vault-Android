package com.lobstr.stellar.vault.presentation.home.settings

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.settings.SettingsInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.settings.SettingsModule
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
        viewState.setupSettingsData(
            interactor.getUserPublicKey(),
            interactor.getSignedAccount() ?: "0",
            BuildConfig.VERSION_NAME
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

    fun signersClicked() {
        viewState.showSignersScreen()
    }

    fun mnemonicsClicked() {
        viewState.showMnemonicsScreen()
    }

    fun deviceLockClicked() {
        viewState.showDeviceLockScreen()
    }

    fun helpClicked() {
        viewState.showHelpScreen()
    }
}