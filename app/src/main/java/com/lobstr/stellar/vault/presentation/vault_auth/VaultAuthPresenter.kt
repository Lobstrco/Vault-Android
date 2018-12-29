package com.lobstr.stellar.vault.presentation.vault_auth

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.vault_auth.VaultAuthModule
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.network.WorkerManager
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class VaultAuthPresenter : BasePresenter<VaultAuthView>() {

    @Inject
    lateinit var interactor: VaultAuthInteractor

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    private var authorizationInProcess = false

    init {
        LVApplication.sAppComponent.plusVaultAuthComponent(VaultAuthModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        registerEventProvider()
        checkAuthorization()
    }

    private fun checkAuthorization() {
        if (interactor.isUserAuthorized()) {
            interactor.registerFcm()
            viewState.showSignerInfoFragment(interactor.getUserPublicKey()!!)
            // FIXME remove in future for debug
            if (BuildConfig.BUILD_TYPE == Constant.BuildType.DEBUG) emulateSignSuccess()
        } else {
            tryAuthorizeVault()
        }
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.notificationEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Notification.Type.SIGNED_NEW_ACCOUNT -> {
                            //TODO use account data if needed
                            val account = it.data as? Account
                            viewState.showHomeScreen()
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
        unsubscribeOnDestroy(
            eventProviderModule.networkEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Network.Type.CONNECTED -> {
                            if (needCheckConnectionState) {
                                tryAuthorizeVault()
                            }
                            needCheckConnectionState = false
                            WorkerManager.cancelWorkById(networkWorkerId)
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun tryAuthorizeVault() {
        if (authorizationInProcess) {
            return
        }
        unsubscribeOnDestroy(
            interactor.authorizeVault()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog()
                }
                .doOnEvent { _, _ ->
                    viewState.dismissProgressDialog()
                    authorizationInProcess = false
                }
                .subscribe({
                    viewState.showSignerInfoFragment(interactor.getUserPublicKey()!!)
                    // FIXME remove in future for debug
                    if (BuildConfig.BUILD_TYPE == Constant.BuildType.DEBUG) emulateSignSuccess()
                }, {
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is DefaultException -> {
                            viewState.showMessage(it.details)
                        }
                        else -> {
                            viewState.showMessage(it.message ?: "")
                        }
                    }
                })
        )
    }

    // FIXME remove after notifications logic
    private fun emulateSignSuccess() {
        unsubscribeOnDestroy(
            Completable.complete()
                .delay(5000.toLong(), TimeUnit.MILLISECONDS)
                .doOnComplete {
                    interactor.confirmIsUserSignerForLobstr()
                    viewState.showHomeScreen()
                }
                .subscribe()
        )
    }
}