package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.signed_account.SignedAccountInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.signed_account.SignedAccountModule
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SignedAccountsPresenter : BasePresenter<SignedAccountsView>() {

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    @Inject
    lateinit var interactor: SignedAccountInteractor

    private val accounts: MutableList<Account> = mutableListOf()

    // For restore RecycleView position after saveInstanceState (-1 - undefined state).
    private var savedRvPosition: Int = Constant.Util.UNDEFINED_VALUE

    private var stellarAccountsSubscription: Disposable? = null
    private val cashedStellarAccounts: MutableList<Account> = mutableListOf()

    init {
        LVApplication.appComponent.plusSignedAccountComponent(SignedAccountModule()).inject(this)
    }

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
    }

    private fun loadSignedAccountsList() {
        unsubscribeOnDestroy(
            interactor.getSignedAccounts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showProgress(true) }
                .doOnEvent { _, _ -> viewState.showProgress(false) }
                .subscribe({
                    // reset saved scroll position for avoid scroll to wrong position after
                    // pagination action
                    savedRvPosition = Constant.Util.UNDEFINED_VALUE

                    accounts.clear()
                    accounts.addAll(it)

                    viewState.showEmptyState(accounts.isEmpty())

                    // check cashed federation items
                    accounts.forEachIndexed { index, accountItem ->
                        val federation =
                            cashedStellarAccounts.find { account -> account.address == accountItem.address }
                                ?.federation
                        accounts[index].federation = federation
                    }
                    viewState.notifyAdapter(accounts)

                    // try receive federations for accounts
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
     * Used for receive federation by account id.
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