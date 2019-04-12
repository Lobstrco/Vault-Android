package com.lobstr.stellar.vault.presentation.vault_auth.recheck_signer

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.re_check_signer.RecheckSignerInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.re_check_signer.RecheckSignerModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class RecheckSignerPresenter : BasePresenter<RecheckSignerView>() {

    @Inject
    lateinit var interactor: RecheckSignerInteractor

    private var recheckSignersInProcess = false

    init {
        LVApplication.sAppComponent.plusRecheckSignerComponent(RecheckSignerModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupUserPublicKey(interactor.getUserPublicKey())
    }

    fun recheckClicked() {
        recheckSigners()
    }

    private fun recheckSigners() {
        if (recheckSignersInProcess) {
            return
        }

        unsubscribeOnDestroy(
            interactor.getSignedAccounts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    recheckSignersInProcess = true
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showProgressDialog(false)
                    recheckSignersInProcess = false
                }
                .subscribe({
                    if (it.isEmpty()) {
                        // Add action if needed
//                        viewState.showMessage(R.string.text_tv_re_check_signer_description)
                    } else {
                        interactor.confirmAccountHasSigners()
                        viewState.showHomeScreen()
                    }
                }, {
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is UserNotAuthorizedException -> {
                            recheckSigners()
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
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.LOG_OUT -> {
                interactor.clearUserData()
                viewState.showAuthScreen()
            }
        }
    }
}