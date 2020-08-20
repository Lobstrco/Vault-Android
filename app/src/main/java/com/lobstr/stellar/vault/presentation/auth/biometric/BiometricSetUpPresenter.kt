package com.lobstr.stellar.vault.presentation.auth.biometric

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.biometric.BiometricSetUpInteractor
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.PinMode.CREATE
import com.lobstr.stellar.vault.presentation.util.biometric.BiometricUtils
import moxy.MvpPresenter

class BiometricSetUpPresenter(private val interactor: BiometricSetUpInteractor) : MvpPresenter<BiometricSetUpView>() {

    var pinMode: Byte = Constant.PinMode.ENTER

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setWindowBackground()
        viewState.windowLightNavigationBar(true)
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
        checkBehavior()
    }

    private fun checkBehavior(){
        // NOTE Skip pin appearance check for prevent pin screen duplication after finish.
        LVApplication.checkPinAppearance = false
        when(pinMode){
            CREATE -> viewState.showVaultAuthScreen()
            else -> viewState.finishScreen()
        }
    }
}