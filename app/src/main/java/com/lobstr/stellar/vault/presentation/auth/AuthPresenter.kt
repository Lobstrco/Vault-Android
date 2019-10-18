package com.lobstr.stellar.vault.presentation.auth

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.util.Constant

@InjectViewState
class AuthPresenter(private val targetFr: Int) : MvpPresenter<AuthView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar(
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_primary
        )

        navigateTo()
    }

    private fun navigateTo() {
        when (targetFr) {
            Constant.Navigation.AUTH -> viewState.showAuthFragment()

            Constant.Navigation.BIOMETRIC_SET_UP -> viewState.showBiometricSetUpFragment()
        }
    }
}