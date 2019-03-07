package com.lobstr.stellar.vault.presentation.vault_auth

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.vault_auth.VaultAuthModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

        viewState.setupToolbar(
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_primary
        )

        registerEventProvider()
        checkAuthorization()
    }

    private fun checkAuthorization() {
        val userToken = interactor.getUserToken()

        if (userToken.isNullOrEmpty()) {
            tryAuthorizeVault()
        } else {
            interactor.registerFcm()
            viewState.showSignerInfoFragment()
            // silent recheck signers for first view attach
            recheckSigners(userToken)
        }
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.notificationEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Notification.Type.SIGNED_NEW_ACCOUNT -> {
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
                            cancelNetworkWorker()
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
                    authorizationInProcess = true
                    viewState.setBtnRetryVisibility(false)
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showProgressDialog(false)
                    authorizationInProcess = false
                }
                .subscribe({
                    if (it.isEmpty()) {
                        viewState.showSignerInfoFragment()
                    } else {
                        interactor.confirmAccountHasSigners()
                        viewState.showHomeScreen()
                    }
                }, {
                    viewState.setBtnRetryVisibility(true)
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

    private fun recheckSigners(token: String) {
        unsubscribeOnDestroy(
            interactor.getSignedAccounts(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showProgressDialog(false)
                }
                .subscribe({
                    if (it.isEmpty()) {
                        // handle if needed
                    } else {
                        interactor.confirmAccountHasSigners()
                        viewState.showHomeScreen()
                    }
                }, {
                    when (it) {
                        is UserNotAuthorizedException -> {
                            recheckSigners(token)
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

    fun logOutClicked() {
        viewState.showLogOutDialog()
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        if (tag.isNullOrEmpty()) {
            return
        }

        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.LOG_OUT -> {
                interactor.clearUserData()
                viewState.showAuthScreen()
            }
        }
    }
}