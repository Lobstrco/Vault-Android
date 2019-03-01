package com.lobstr.stellar.vault.presentation.home.dashboard

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.dashboard.DashboardInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.dashboard.DashboardModule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class DashboardPresenter : BasePresenter<DashboardView>() {

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    @Inject
    lateinit var interactor: DashboardInteractor

    lateinit var signerKey: String

    init {
        LVApplication.sAppComponent.plusDashboardComponent(DashboardModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.showPublicKey(interactor.getUserPublicKey())
        registerEventProvider()
        loadPendingTransactions()
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
                                loadPendingTransactions()
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
        unsubscribeOnDestroy(
            eventProviderModule.notificationEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Notification.Type.TRANSACTION_COUNT_CHANGED -> {
                            loadPendingTransactions()
                            // FIXME SIGNED_NEW_ACCOUNT don't worked. Need additional notification about SIGNERS_COUNT_CHANGED. Temporally solution for update signers count
                            loadSignedAccountsList()
                        }
                        Notification.Type.ADDED_NEW_TRANSACTION -> {
                            loadPendingTransactions()
                            // FIXME SIGNED_NEW_ACCOUNT don't worked. Need additional notification about SIGNERS_COUNT_CHANGED. Temporally solution for update signers count
                            loadSignedAccountsList()
                        }
                        Notification.Type.SIGNED_NEW_ACCOUNT -> loadSignedAccountsList()
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun loadPendingTransactions() {
        unsubscribeOnDestroy(
            interactor.getPendingTransactionList(null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.showDashboardInfo(it.count)
                }, {
                    viewState.showDashboardInfo(0)
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showErrorMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is UserNotAuthorizedException -> {
                            loadPendingTransactions()
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

    private fun loadSignedAccountsList() {
        unsubscribeOnDestroy(
            interactor.getSignedAccounts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // FIXME Notify about signers count changed: for updated signers count in settings
                    eventProviderModule.notificationEventSubject.onNext(
                        Notification(Notification.Type.SIGNERS_COUNT_CHANGED, null)
                    )

                    viewState.hideSignersProgress()
                    if (it.size == 1) {
                        signerKey = it[0].address
                        viewState.showSignersPublickKey(signerKey)
                    } else {
                        viewState.showSignersCount(interactor.getSignersCount())
                    }
                }, {
                    viewState.showSignersCount(interactor.getSignersCount())

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

    fun transactionCountClicked() {
        viewState.navigateToTransactionList()
    }

    fun showTransactionListClicked() {
        viewState.navigateToTransactionList()
    }

    fun copyKeyClicked() {
        viewState.copyData(interactor.getUserPublicKey())
    }

    fun copySignerClicked() {
        viewState.copyData(signerKey)
    }

    fun userVisibleHintCalled(visible: Boolean) {
        if (visible) {
            loadPendingTransactions()
            loadSignedAccountsList()
        }
    }
}