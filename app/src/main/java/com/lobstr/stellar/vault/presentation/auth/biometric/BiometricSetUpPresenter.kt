package com.lobstr.stellar.vault.presentation.auth.biometric

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.biometric.BiometricSetUpInteractor
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.biometric.BiometricSetUpModule
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricUtils
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class BiometricSetUpPresenter : MvpPresenter<BiometricSetUpView>() {

    @Inject
    lateinit var interactor: BiometricSetUpInteractor

    init {
        LVApplication.appComponent.plusBiometricSetUpComponent(BiometricSetUpModule()).inject(this)
    }

    fun skipClicked() {
        interactor.setBiometricEnabled(false)
        viewState.showVaultAuthScreen()
    }

    fun turnOnClicked() {
        if (BiometricUtils.isBiometricAvailable(AppUtil.getAppContext())) {
            viewState.showBiometricDialog(true)
        } else {
            viewState.showBiometricInfoDialog(
                R.string.title_biometric_not_set_up_dialog,
                R.string.msg_biometric_not_set_up_dialog
            )
        }
    }

    override fun detachView(view: BiometricSetUpView?) {
        viewState.showBiometricDialog(false)
        super.detachView(view)
    }

    fun biometricAuthenticationSuccessful() {
        interactor.setBiometricEnabled(true)
        viewState.showVaultAuthScreen()
    }
}