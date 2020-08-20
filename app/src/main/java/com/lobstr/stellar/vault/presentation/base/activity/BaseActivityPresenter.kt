package com.lobstr.stellar.vault.presentation.base.activity

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.domain.base.BaseInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.util.AppUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class BaseActivityPresenter(
    private val interactor: BaseInteractor,
    private val mEventProviderModule: EventProviderModule
) : BasePresenter<BaseActivityView>() {

    private var authInProcess = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        registerEventProvider()
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            mEventProviderModule.authEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // Try auth.
                    authViaTangem()

                }, {
                    it.printStackTrace()
                })
        )
    }

    fun setActionBarTitle(title: String?) {
        viewState.setActionBarTitle(title)
    }

    fun changeHomeBtnVisibility(visible: Boolean) {
        viewState.changeActionBarIconVisibility(visible)
    }

    /**
     * Try authenticate via Tangem after [com.lobstr.stellar.vault.domain.util.event.Auth] event
     * received from [com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException]
     * with [com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException.Action.AUTH_REQUIRED] action.
     */
    private fun authViaTangem() {
        if (authInProcess) {
            return
        }
        unsubscribeOnDestroy(
            interactor.getChallenge()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    authInProcess = true
                }
                .doOnEvent { _, _ ->
                    authInProcess = false
                }
                .subscribe({
                    viewState.showTangemScreen(
                        TangemInfo().apply {
                            accountId = interactor.getUserPublicKey()
                            cardId = interactor.getTangemCardId()
                            pendingTransaction = it
                            message =
                                AppUtil.getString(R.string.text_tv_tangem_dialog_tittle_session_expired)
                            description =
                                AppUtil.getString(R.string.text_tv_tangem_dialog_description_session_expired)
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

    fun handleTangemInfo(tangemInfo: TangemInfo?) {
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
                        // Notify about update content event.
                        mEventProviderModule.updateEventSubject.onNext(Update())
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

    fun checkPinAppearance() {
        if (attachedViews.size == 0) return

        if (interactor.hasEncryptedPin()) {
            viewState.proceedPinActivityAppearance()
        }
    }
}