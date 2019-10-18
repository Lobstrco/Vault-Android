package com.lobstr.stellar.vault.presentation.auth.biometric

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.biometric.BiometricSetUpInteractor
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.biometric.BiometricSetUpModule
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricUtils
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
        if (BiometricUtils.isBiometricAvailable(LVApplication.appComponent.context)) {
            viewState.showBiometricDialog(true)
        } else {
            viewState.showBiometricInfoDialog(
                R.string.title_finger_print_dialog,
                R.string.msg_finger_print_dialog
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