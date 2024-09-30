package com.lobstr.stellar.vault.presentation.auth.biometric

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.biometric.BiometricSetUpInteractor
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.PinMode.CREATE
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricUtils
import moxy.MvpPresenter
import javax.inject.Inject

class BiometricSetUpPresenter @Inject constructor(private val interactor: BiometricSetUpInteractor) :
    MvpPresenter<BiometricSetUpView>() {

    var pinMode: Byte = Constant.PinMode.ENTER

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setWindowBackground()
        viewState.setupNavigationBar(true)
        viewState.setupToolbar(
            false,
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_primary
        )
    }

    fun skipClicked() {
        interactor.setBiometricEnabled(false)
        checkBehavior()
    }

    fun turnOnClicked() {
        if (BiometricUtils.isBiometricAvailable(AppUtil.getAppContext())) {
            viewState.showBiometricDialog()
        } else {
            viewState.showBiometricInfoDialog(
                R.string.biometric_info_not_set_up_title,
                R.string.biometric_info_not_set_up_description
            )
        }
    }

    fun biometricAuthenticationSuccessful() {
        interactor.setBiometricEnabled(true)
        checkBehavior()
    }

    private fun checkBehavior() {
        // NOTE Skip pin appearance check for prevent pin screen duplication after finish.
        LVApplication.checkPinAppearance = false
        when (pinMode) {
            CREATE -> viewState.showVaultAuthScreen()
            else -> viewState.finishScreen()
        }
    }

    fun onBackPressed() {
        when (pinMode) {
            Constant.PinMode.ENTER -> viewState.finishApp()
            else -> viewState.finishScreen()
        }
    }
}