package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.signed_account.SignedAccountInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Auth
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class SignedAccountsPresenter @Inject constructor(
    private val interactor: SignedAccountInteractor,
    private val eventProviderModule: EventProviderModule
) : BasePresenter<SignedAccountsView>() {

    private var stellarAccountsDisposable: Disposable? = null

    private val stellarAccounts: MutableList<Account> = mutableListOf()
    private val cachedStellarAccounts: MutableList<Account> = mutableListOf()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.title_toolbar_signed_accounts)
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
                        Notification.Type.REMOVED_SIGNER, Notification.Type.SIGNED_NEW_ACCOUNT -> loadSignedAccountsList()
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
                        Update.Type.ACCOUNT_NAME -> {
                            unsubscribeOnDestroy(Completable.fromCallable {
                                checkAccountNames(stellarAccounts)
                            }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    { viewState.notifyAdapter(stellarAccounts) },
                                    Throwable::printStackTrace
                                )
                            )
                        }
                        else -> loadSignedAccountsList()
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun loadSignedAccountsList() {
        unsubscribeOnDestroy(
            interactor.getSignedAccounts()
                .doOnSuccess {
                    stellarAccounts.apply {
                        clear()
                        addAll(it)
                    }

                    if(stellarAccounts.isEmpty()) {
                        interactor.clearAccountNames()
                    }

                    checkAccountNames(stellarAccounts)

                    // check cached federation items
                    stellarAccounts.forEachIndexed { index, accountItem ->
                        val federation =
                            cachedStellarAccounts.find { account -> account.address == accountItem.address }
                                ?.federation
                        stellarAccounts[index].federation = federation
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showProgress(true) }
                .doOnEvent { _, _ -> viewState.showProgress(false) }
                .subscribe({
                    viewState.showEmptyState(stellarAccounts.isEmpty())

                    viewState.notifyAdapter(stellarAccounts)

                    // Try receive federations for accounts.
                    getStellarAccounts(stellarAccounts)

                    viewState.scrollListToPosition(0)
                }, {
                    viewState.showEmptyState(stellarAccounts.isEmpty())
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
     *  TODO Places: Dashboard, SignedAccounts, Tr Details, Tr list.
     *   Clear: Log Out and accounts list is empty.
     */
    private fun checkAccountNames(accounts: List<Account>) {
        val names = interactor.getAccountNames()
        for(account in accounts) {
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

                // Check cached federation items
                accounts.forEachIndexed { index, accountItem ->
                    val federation =
                        cachedStellarAccounts.find { account -> account.address == accountItem.address }
                            ?.federation
                    accounts[index].federation = federation
                }

                viewState.notifyAdapter(accounts)
            }, {
                // Ignore.
            })

        unsubscribeOnDestroy(stellarAccountsDisposable!!)
    }

    fun onRefreshCalled() {
        loadSignedAccountsList()
    }

    fun signedAccountItemClicked(account: Account) {
        viewState.showEditAccountDialog(account.address)
    }

    fun signedAccountItemLongClicked(account: Account) {
        viewState.copyToClipBoard(account.address)
    }
}