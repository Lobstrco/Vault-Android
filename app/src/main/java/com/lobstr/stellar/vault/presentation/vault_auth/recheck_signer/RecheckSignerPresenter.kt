package com.lobstr.stellar.vault.presentation.vault_auth.recheck_signer

import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.re_check_signer.RecheckSignerInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.re_check_signer.RecheckSignerModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RecheckSignerPresenter : BasePresenter<RecheckSignerView>() {

    @Inject
    lateinit var interactor: RecheckSignerInteractor

    private var recheckSignersInProcess = false

    init {
        LVApplication.appComponent.plusRecheckSignerComponent(RecheckSignerModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupUserPublicKey(AppUtil.ellipsizeStrInMiddle(interactor.getUserPublicKey(), PK_TRUNCATE_COUNT))
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
                    if (it.isNotEmpty()) {
                        interactor.confirmAccountHasSigners()
                        viewState.showHomeScreen()
                    }
                }, {
                    when (it) {
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