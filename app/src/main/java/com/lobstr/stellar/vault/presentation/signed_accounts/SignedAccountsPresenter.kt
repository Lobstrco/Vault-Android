package com.lobstr.stellar.vault.presentation.signed_accounts

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.signed_account.SignedAccountInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.signed_account.SignedAccountModule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class SignedAccountsPresenter : BasePresenter<SignedAccountsView>() {

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    @Inject
    lateinit var mInteractor: SignedAccountInteractor

    init {
        LVApplication.sAppComponent.plusSignedAccountComponent(SignedAccountModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.signed_accounts)
        viewState.initRecycledView()
        registerEventProvider()
        loadSignedAccountsList()
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.networkEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Network.Type.CONNECTED -> {
                            if (needCheckConnectionState) {
                                loadSignedAccountsList()
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

    private fun loadSignedAccountsList() {
        unsubscribeOnDestroy(
            mInteractor.getSignedAccounts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showProgress() }
                .doOnEvent { _, _ -> viewState.hideProgress() }
                .subscribe({
                    if (it.isEmpty()) {
                        viewState.showEmptyState()
                    } else {
                        viewState.hideEmptyState()
                    }
                    viewState.notifyAdapter(it)
                }, {
                    viewState.showEmptyState()
                    viewState.notifyAdapter(emptyList())

                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showErrorMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is UserNotAuthorizedException -> {
                            loadSignedAccountsList()
                        }
                        is DefaultException -> {
                            viewState.showErrorMessage(it.details)
                        }
                        else -> {
                            viewState.showErrorMessage(it.message ?: "")
                        }
                    }
                })
        )
    }

    fun onRefreshCalled() {
        loadSignedAccountsList()
    }
}