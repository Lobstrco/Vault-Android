package com.lobstr.stellar.vault.presentation.home.dashboard

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.dashboard.DashboardInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dager.module.dashboard.DashboardModule
import com.lobstr.stellar.vault.presentation.util.manager.network.WorkerManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class DashboardPresenter : BasePresenter<DashboardView>() {

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    @Inject
    lateinit var dashboardInteractor: DashboardInteractor

    init {
        LVApplication.sAppComponent.plusDashboardComponent(DashboardModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.dashboard)
        registerEventProvider()
        loadPendingTransactions()
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
                            }
                            needCheckConnectionState = false
                            WorkerManager.cancelWorkById(networkWorkerId)
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
                        Notification.Type.ADDED_NEW_TRANSACTION -> {
                            loadPendingTransactions()
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun loadPendingTransactions() {
        unsubscribeOnDestroy(
            dashboardInteractor.getPendingTransactionList(null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showProgress() }
                .doOnEvent { _, _ -> viewState.hideProgress() }
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

    fun refreshCalled() {
        loadPendingTransactions()
    }
}