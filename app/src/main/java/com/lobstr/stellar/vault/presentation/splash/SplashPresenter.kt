package com.lobstr.stellar.vault.presentation.splash

import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.domain.splash.SplashInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.splash.SplashModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.Completable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashPresenter : BasePresenter<SplashView>() {

    companion object {
        const val SPLASH_TIMEOUT = 2000
    }

    @Inject
    lateinit var interactor: SplashInteractor

    init {
        LVApplication.appComponent.plusSplashComponent(SplashModule()).inject(this)
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
            interactor.hasMnemonics() && interactor.hasEncryptedPin() -> viewState.showPinScreen()
            !interactor.hasMnemonics() && interactor.hasPublicKey() -> {
                // Case for Tangem implementation.
                when {
                    interactor.hasAuthToken() -> viewState.showHomeScreen()
                    else -> viewState.showVaultAuthScreen()
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