package com.lobstr.stellar.vault.presentation.home.dashboard

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
import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class DashboardPresenter : BasePresenter<DashboardView>() {

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    @Inject
    lateinit var interactor: DashboardInteractor

    private var stellarAccountsSubscription: Disposable? = null
    private val cashedStellarAccounts: MutableList<Account> = mutableListOf()

    init {
        LVApplication.appComponent.plusDashboardComponent(DashboardModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.initSignedAccountsRecycledView()
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
                            cancelNetworkWorker(false)
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
                        Notification.Type.TRANSACTION_COUNT_CHANGED,
                        Notification.Type.ADDED_NEW_TRANSACTION,
                        Notification.Type.ADDED_NEW_SIGNATURE,
                        Notification.Type.TRANSACTION_SUBMITTED -> {
                            loadPendingTransactions()
                        }
                        Notification.Type.SIGNED_NEW_ACCOUNT, Notification.Type.REMOVED_SIGNER -> {
                            loadSignedAccountsList()
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
                .doOnEvent { _, _ -> viewState.showSignersProgress(false) }
                .subscribe({
                    viewState.showSignersCount(interactor.getSignersCount())

                    // check cashed federation items
                    it.forEachIndexed { index, accountItem ->
                        val federation =
                            cashedStellarAccounts.find { account -> account.address == accountItem.address }
                                ?.federation
                        it[index].federation = federation
                    }

                    viewState.notifySignedAccountsAdapter(it)

                    // try receive federations for accounts
                    getStellarAccounts(it)
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

    /**
     * Used for receive federation by account id
     */
    private fun getStellarAccounts(accounts: List<Account>) {
        stellarAccountsSubscription?.dispose()
        stellarAccountsSubscription = Observable.fromIterable(accounts)
            .subscribeOn(Schedulers.io())
            .filter { account: Account ->
                cashedStellarAccounts
                    .find { cashedAccount -> cashedAccount.address == account.address } == null
            }
            .flatMapSingle {
                interactor.getStellarAccount(it.address).onErrorReturnItem(it)
            }
            .filter { it.federation != null }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.forEach { account ->
                    if (cashedStellarAccounts.find { cashedAccount -> cashedAccount.address == account.address } == null) {
                        cashedStellarAccounts.add(account)
                    }
                }

                // check cashed federation items
                accounts.forEachIndexed { index, accountItem ->
                    val federation =
                        cashedStellarAccounts.find { account -> account.address == accountItem.address }
                            ?.federation
                    accounts[index].federation = federation
                }

                viewState.notifySignedAccountsAdapter(accounts)
            }, {
                // ignore
            })

        unsubscribeOnDestroy(stellarAccountsSubscription!!)
    }

    fun transactionCountClicked() {
        viewState.navigateToTransactionList()
    }

    fun showTransactionListClicked() {
        viewState.navigateToTransactionList()
    }

    fun copyKeyClicked() {
        viewState.copyToClipBoard(interactor.getUserPublicKey())
    }

    fun userVisibleHintCalled(visible: Boolean) {
        if (visible) {
            loadPendingTransactions()
            loadSignedAccountsList()
        }
    }

    fun signersCountClicked() {
        if (interactor.getSignersCount() > 1) {
            viewState.showSignersScreen()
        }
    }

    fun signedAccountItemClicked(account: Account) {
        viewState.showEditAccountDialog(account.address)
    }

    fun signedAccountItemLongClicked(account: Account) {
        viewState.copyToClipBoard(account.address)
    }
}