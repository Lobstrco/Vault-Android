package com.lobstr.stellar.vault.presentation.splash

import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.domain.splash.SplashInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.rxjava3.core.Completable
import java.util.concurrent.TimeUnit

class SplashPresenter(private val interactor: SplashInteractor) : BasePresenter<SplashView>() {

    companion object {
        const val SPLASH_TIMEOUT = 2000
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        unsubscribeOnDestroy(
            Completable.complete()
                .delay(SPLASH_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .doOnComplete {
                    if (BuildConfig.FLAVOR == Constant.Flavor.QA) {
                        viewState.showFlavorDialog(
                            BuildConfig.FLAVOR.toUpperCase().plus(" ").plus(AppUtil.getAppBehavior())
                        )
                    } else {
                        checkAppBehavior()
                    }
                }
                .subscribe()
        )
    }

    private fun checkAppBehavior() {
        when {
            interactor.hasPublicKey() -> {
                when {
                    interactor.hasMnemonics() && !interactor.hasEncryptedPin() ->{
                        // Force logout user (pin don't setup).
                        interactor.clearUserData()
                        viewState.showAuthScreen()
                    }
                    else -> {
                        when {
                            interactor.hasAuthToken() -> viewState.showHomeScreen()
                            else -> viewState.showVaultAuthScreen()
                        }
                    }
                }
            }
            else -> {
                // In all cases - clear all pre-setup data.
                interactor.clearUserData()
                viewState.showAuthScreen()
            }
        }
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.INFO -> {
                checkAppBehavior()
            }
        }
    }
}