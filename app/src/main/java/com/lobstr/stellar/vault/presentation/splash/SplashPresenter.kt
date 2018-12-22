package com.lobstr.stellar.vault.presentation.splash

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.domain.splash.SplashInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dager.module.splash.SplashModule
import io.reactivex.Completable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class SplashPresenter : BasePresenter<SplashView>() {

    companion object {
        const val SPLASH_TIMEOUT = 2000
    }

    @Inject
    lateinit var interactor: SplashInteractor

    init {
        LVApplication.sAppComponent.plusSplashComponent(SplashModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        unsubscribeOnDestroy(
            Completable.complete()
                .delay(SPLASH_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .doOnComplete {
                    if (interactor.isUserAuthorized()) {
                        viewState.showPinScreen()
                    } else {
                        viewState.showAuthScreen()
                    }
                }
                .subscribe()
        )
    }
}