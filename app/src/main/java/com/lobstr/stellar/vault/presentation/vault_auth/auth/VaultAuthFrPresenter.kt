package com.lobstr.stellar.vault.presentation.vault_auth.auth

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.vault_auth.VaultAuthModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class VaultAuthFrPresenter : BasePresenter<VaultAuthFrView>() {

    @Inject
    lateinit var interactor: VaultAuthInteractor

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    init {
        LVApplication.appComponent.plusVaultAuthComponent(VaultAuthModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showIdentityContent(!interactor.hasMnemonics())
        viewState.setupInfo(
            interactor.hasMnemonics(),
            interactor.hasTangem(),
            Constant.Social.USER_ICON_LINK.plus(interactor.getUserPublicKey()).plus(".png"),
            if (interactor.hasMnemonics()) {
                AppUtil.ellipsizeStrInMiddle(
                    interactor.getUserPublicKey(),
                    Constant.Util.PK_TRUNCATE_COUNT
                )
            } else {
                AppUtil.getString(R.string.text_tv_vault_auth_signer_card_title)
            },
            if (interactor.hasMnemonics()) {
                null
            } else {
                AppUtil.ellipsizeStrInMiddle(
                    interactor.getUserPublicKey(),
                    Constant.Util.PK_TRUNCATE_COUNT
                )
            },
            if (interactor.hasMnemonics()) {
                AppUtil.getString(R.string.text_tv_vault_auth_signer_mnemonics_description)
            } else {
                AppUtil.getString(R.string.text_tv_vault_auth_signer_card_description)
            },
            if (interactor.hasMnemonics()) {
                AppUtil.getString(R.string.text_btn_vault_auth_mnemonics)
            } else {
                AppUtil.getString(R.string.text_btn_vault_auth_signer_card)
            }
        )
        registerEventProvider()
        checkAuthorization()
    }

    private fun checkAuthorization(silentCheck: Boolean = true) {
        val userToken = interactor.getUserToken()

        if (userToken.isNullOrEmpty()) {
            when {
                interactor.hasMnemonics() -> tryAuthorizeVault()
                interactor.hasTangem() -> if (!silentCheck) authTangem()
            }
        } else {
            viewState.showHomeScreen()
        }
    }

    fun authTangem() {
        // Case for Tangem implementation.
        // Create challenge and receive token.
        unsubscribeOnDestroy(
            interactor.getChallenge()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showProgressDialog(false)
                }
                .subscribe({
                    viewState.showTangemScreen(
                        TangemInfo().apply {
                            accountId = interactor.getUserPublicKey()
                            cardId = interactor.getTangemCardId()
                            pendingTransaction = it
                        }
                    )
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
                                checkAuthorization(false)
                            }
                            cancelNetworkWorker(false)
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun tryAuthorizeVault() {
        unsubscribeOnDestroy(
            interactor.authorizeVault()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showProgressDialog(false)
                }
                .subscribe({
                    viewState.showHomeScreen()
                }, {
                    viewState.showIdentityContent(true)
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

    fun handleTangemInfo(tangemInfo: TangemInfo?) {
        // Try authorize vault.
        if (tangemInfo != null) {
            unsubscribeOnDestroy(
                interactor.authorizeVault(tangemInfo.signedTransaction!!)
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
                            viewState.showHomeScreen()
                        } else {
                            interactor.confirmAccountHasSigners()
                            viewState.showHomeScreen()
                        }
                    }, {
                        when (it) {
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
    }

    fun logOutClicked() {
        viewState.showLogOutDialog(
            if (interactor.hasMnemonics()) {
                AppUtil.getString(R.string.title_log_out_mnemonics_dialog)
            } else {
                null
            },
            if (interactor.hasMnemonics()) {
                AppUtil.getString(R.string.msg_log_out_mnemonics_dialog)
            } else {
                AppUtil.getString(R.string.msg_log_out_dialog)
            }
        )
    }

    fun authClicked() {
        checkAuthorization(false)
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.LOG_OUT -> {
                interactor.clearUserData()
                viewState.showAuthScreen()
            }
        }
    }
}