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
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SignedAccountsPresenter(
    private val interactor: SignedAccountInteractor,
    private val eventProviderModule: EventProviderModule
) : BasePresenter<SignedAccountsView>() {

    private val accounts: MutableList<Account> = mutableListOf()

    // For restore RecycleView position after saveInstanceState (-1 - undefined state).
    private var savedRvPosition: Int = Constant.Util.UNDEFINED_VALUE

    private var stellarAccountsSubscription: Disposable? = null
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
                    loadSignedAccountsList()
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun loadSignedAccountsList() {
        unsubscribeOnDestroy(
            interactor.getSignedAccounts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showProgress(true) }
                .doOnEvent { _, _ -> viewState.showProgress(false) }
                .subscribe({
                    // Reset saved scroll position for avoid scroll to wrong position after
                    // pagination action.
                    savedRvPosition = Constant.Util.UNDEFINED_VALUE

                    accounts.clear()
                    accounts.addAll(it)

                    viewState.showEmptyState(accounts.isEmpty())

                    // check cached federation items
                    accounts.forEachIndexed { index, accountItem ->
                        val federation =
                            cachedStellarAccounts.find { account -> account.address == accountItem.address }
                                ?.federation
                        accounts[index].federation = federation
                    }
                    viewState.notifyAdapter(accounts)

                    // Try receive federations for accounts.
                    getStellarAccounts(accounts)

                    viewState.scrollListToPosition(0)
                }, {
                    viewState.showEmptyState(accounts.isEmpty())
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showErrorMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is UserNotAuthorizedException -> {
                            when (it.action) {
                                UserNotAuthorizedException.Action.AUTH_REQUIRED -> eventProviderModule.authEventSubject.onNext(Auth())
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
     * Used for receive federation by account id.
     */
    private fun getStellarAccounts(accounts: List<Account>) {
        stellarAccountsSubscription?.dispose()
        stellarAccountsSubscription = Observable.fromIterable(accounts)
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

        unsubscribeOnDestroy(stellarAccountsSubscription!!)
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

    fun onSaveInstanceState(position: Int) {
        // Save list position and restore it after if needed.
        savedRvPosition = position
    }

    fun attemptRestoreRvPosition() {
        if (savedRvPosition == Constant.Util.UNDEFINED_VALUE) {
            return
        }

        viewState.scrollListToPosition(savedRvPosition)

        savedRvPosition = Constant.Util.UNDEFINED_VALUE
    }
}