package com.lobstr.stellar.vault.presentation.home.dashboard

import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.dashboard.DashboardInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Auth
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.domain.util.event.Update.Type.ACCOUNT_NAME
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class DashboardPresenter @Inject constructor(
    private val interactor: DashboardInteractor,
    private val eventProviderModule: EventProviderModule
) : BasePresenter<DashboardView>() {

    private var stellarAccountsDisposable: Disposable? = null

    private val stellarAccounts: MutableList<Account> = mutableListOf()
    private val cachedStellarAccounts: MutableList<Account> = mutableListOf()

    private var loadSignedAccountsInProcess = false
    private var loadTransactionsInProcess = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.initSignedAccountsRecycledView()

        val vaultPublicKey = interactor.getUserPublicKey()

        viewState.showVaultInfo(
            interactor.hasTangem(),
            Constant.Social.USER_ICON_LINK.plus(vaultPublicKey).plus(".png"),
            AppUtil.ellipsizeStrInMiddle(vaultPublicKey, PK_TRUNCATE_COUNT)
        )

        registerEventProvider()
        loadSignedAccountsAndTransactions()
    }

    private fun loadSignedAccountsAndTransactions() {
        viewState.showRefreshAnimation(true)
        loadSignedAccountsList()
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
                                loadSignedAccountsAndTransactions()
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
                            loadSignedAccountsAndTransactions()
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )

        unsubscribeOnDestroy(
            eventProviderModule.updateEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        ACCOUNT_NAME -> {
                            unsubscribeOnDestroy(Completable.fromCallable {
                                checkAccountNames(stellarAccounts)
                            }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    { viewState.notifySignedAccountsAdapter(stellarAccounts) },
                                    Throwable::printStackTrace
                                )
                            )
                        }
                        else -> loadSignedAccountsAndTransactions()
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun loadPendingTransactions() {
        if (loadTransactionsInProcess) {
            return
        }

        unsubscribeOnDestroy(
            interactor.getPendingTransactionList(null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadTransactionsInProcess = true }
                .doOnEvent { _, _ -> loadTransactionsInProcess = false }
                .subscribe({
                    viewState.showDashboardInfo(it.count)
                }, {
                    viewState.showDashboardInfo(null)
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showErrorMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is UserNotAuthorizedException -> {
                            when (it.action) {
                                UserNotAuthorizedException.Action.AUTH_REQUIRED -> eventProviderModule.authEventSubject.onNext(
                                    Auth()
                                )
                                else -> loadPendingTransactions()
                            }
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
        if (loadSignedAccountsInProcess) {
            return
        }

        unsubscribeOnDestroy(
            interactor.getSignedAccounts()
                .doOnSuccess {
                    stellarAccounts.apply {
                        clear()
                        addAll(it)
                    }
                    if (stellarAccounts.isEmpty()) {
                        interactor.clearAccountNames()
                    }

                    checkAccountNames(stellarAccounts)

                    // Check cached federation items.
                    stellarAccounts.forEachIndexed { index, accountItem ->
                        val federation =
                            cachedStellarAccounts.find { account -> account.address == accountItem.address }
                                ?.federation
                        stellarAccounts[index].federation = federation
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadSignedAccountsInProcess = true }
                .doOnEvent { _, _ ->
                    loadSignedAccountsInProcess = false
                    viewState.showSignersProgress(false)
                }
                .subscribe({
                    viewState.showSignersCount(stellarAccounts.size)
                    viewState.showSignersEmptyState(stellarAccounts.isEmpty())
                    viewState.notifySignedAccountsAdapter(stellarAccounts)

                    // Try receive federations for accounts.
                    getStellarAccounts(stellarAccounts)
                }, {
                    viewState.showSignersCount(interactor.getSignersCount())
                    viewState.showSignersEmptyState(interactor.getSignersCount() == 0)

                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showErrorMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is UserNotAuthorizedException -> {
                            when (it.action) {
                                UserNotAuthorizedException.Action.AUTH_REQUIRED -> eventProviderModule.authEventSubject.onNext(
                                    Auth()
                                )
                                else -> loadSignedAccountsList()
                            }
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
     * Check Accounts' names from cache.
     */
    private fun checkAccountNames(accounts: List<Account>) {
        val names = interactor.getAccountNames()
        for (account in accounts) {
            account.name = names[account.address]
        }
    }

    /**
     * Used for receive federation by account id.
     */
    private fun getStellarAccounts(accounts: List<Account>) {
        stellarAccountsDisposable?.dispose()
        stellarAccountsDisposable = Observable.fromIterable(accounts)
            .subscribeOn(Schedulers.io())
            .filter { account: Account ->
                cachedStellarAccounts
                    .find { cachedAccount -> cachedAccount.address == account.address } == null
            }
            .flatMapSingle {
                interactor.getStellarAccount(it.address).onErrorReturnItem(it)
            }
            .filter { it.federation != null }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.forEach { account ->
                    if (cachedStellarAccounts.find { cachedAccount -> cachedAccount.address == account.address } == null) {
                        cachedStellarAccounts.add(account)
                    }
                }

                // Check cached federation items.
                accounts.forEachIndexed { index, accountItem ->
                    val federation =
                        cachedStellarAccounts.find { account -> account.address == accountItem.address }
                            ?.federation
                    accounts[index].federation = federation
                }

                viewState.notifySignedAccountsAdapter(accounts)
            }, {
                // Ignore.
            })

        unsubscribeOnDestroy(stellarAccountsDisposable!!)
    }

    fun transactionCountClicked() {
        viewState.navigateToTransactionList()
    }

    fun showTransactionListClicked() {
        viewState.navigateToTransactionList()
    }

    fun copyKeyClicked() {
        interactor.getUserPublicKey()?.let { viewState.copyToClipBoard(it) }
    }

    fun userVisibleHintCalled(visible: Boolean) {
        if (visible) {
            loadSignedAccountsAndTransactions()
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

    fun refreshClicked() {
        loadSignedAccountsAndTransactions()
    }

    fun addAccountClicked() {
        viewState.showSignerInfoScreen()
    }
}