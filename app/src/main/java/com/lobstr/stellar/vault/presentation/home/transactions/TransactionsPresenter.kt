package com.lobstr.stellar.vault.presentation.home.transactions

import com.lobstr.stellar.tsmapper.presentation.util.Constant
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.*
import com.lobstr.stellar.vault.domain.transaction.TransactionInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Auth
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.Status
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class TransactionsPresenter @Inject constructor(
    private val interactor: TransactionInteractor,
    private val eventProviderModule: EventProviderModule
) : BasePresenter<TransactionsView>() {

    companion object {
        const val LIMIT_PAGE_SIZE = 20
    }

    lateinit var status: String

    private var transactionsLoadingDisposable: Disposable? = null
    private var nextPageUrl: String? = null
    private var newLoadTransactions = true
    private var isLoading = false
    private var cancellationInProcess = false
    private val transactions: MutableList<TransactionItem> = mutableListOf()

    private var stellarAccountsDisposable: Disposable? = null
    private val cachedStellarAccounts: MutableList<Account> = mutableListOf()

    private var transactionHashToDelete: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        registerEventProvider()
        viewState.showOptionsMenu(false)
        when (status) {
            Status.PENDING -> {
                viewState.setEmptyState(AppUtil.getString(R.string.transactions_status_pending_empty_state_title))
                viewState.showFab(true)
            }

            Status.SIGNED -> {
                viewState.setEmptyState(AppUtil.getString(R.string.transactions_status_signed_empty_state_title))
                viewState.showFab(true)
            }
            else -> viewState.showFab(false)
        }
        viewState.initRecycledView()
        loadTransactions()
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.networkEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Network.Type.CONNECTED -> {
                            if (needCheckConnectionState) {
                                loadTransactions()
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
                        Notification.Type.ADDED_NEW_TRANSACTION -> {
                            when (status) {
                                Status.PENDING -> refreshCalled()
                            }
                        }
                        Notification.Type.ADDED_NEW_SIGNATURE,
                        Notification.Type.TRANSACTION_SUBMITTED,
                        Notification.Type.SIGNED_NEW_ACCOUNT, Notification.Type.REMOVED_SIGNER -> {
                            refreshCalled()
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
                        Update.Type.ACCOUNT_NAME -> {
                            unsubscribeOnDestroy(Single.fromCallable {
                                checkAccountNames(transactions)
                            }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    { changed -> if (changed) viewState.showTransactionList(transactions, !nextPageUrl.isNullOrEmpty()) },
                                    Throwable::printStackTrace
                                )
                            )
                        }
                        else -> refreshCalled()
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun loadTransactions() {
        // Cancel previous loading.
        transactionsLoadingDisposable?.dispose()

        transactionsLoadingDisposable =
            interactor.getFilteredTransactionsList(
                status,
                notEnoughSignersWeight = when (status) {
                    Status.SIGNED -> false
                    else -> null
                },
                submittedAtIsNull = true,
                excludeOld = when (status) {
                    Status.SIGNED -> true
                    else -> null
                },
                type = when (status) {
                    Status.SIGNED -> Constant.TransactionType.TRANSACTION
                    else -> null
                },
                nextPageUrl,
                LIMIT_PAGE_SIZE
            )
            .doOnSuccess {
                if (newLoadTransactions) {
                    transactions.clear()
                }

                transactions.addAll(it.results)

                checkAccountNames(transactions)

                // Check cached federation items.
                transactions.forEachIndexed { index, transactionItem ->
                    val federation =
                        cachedStellarAccounts.find { account -> account.address == transactionItem.transaction.sourceAccount }
                            ?.federation
                    transactions[index].federation = federation
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                viewState.showOptionsMenu(false)
                isLoading = true
                if (newLoadTransactions) {
                    viewState.showPullToRefresh(true)
                }
            }
            .doOnEvent { _, _ ->
                isLoading = false
                viewState.showPullToRefresh(false)
            }
            .subscribe({ result ->
                viewState.showEmptyState(transactions.isEmpty())

                nextPageUrl = result.next

                viewState.showTransactionList(transactions, !nextPageUrl.isNullOrEmpty())

                // Try receive federations for accounts in transaction.
                getStellarAccounts(transactions)

                if (newLoadTransactions) {
                    viewState.scrollListToPosition(0)
                }
                newLoadTransactions = false
                viewState.showOptionsMenu(transactions.isNotEmpty())
            }, {
                viewState.showOptionsMenu(transactions.isNotEmpty())
                viewState.showEmptyState(transactions.isEmpty())
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
                            else -> loadTransactions()
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

        unsubscribeOnDestroy(transactionsLoadingDisposable!!)
    }

    /**
     * Check Accounts' names from cache.
     * @return true when Accounts' names was changed.
     */
    private fun checkAccountNames(transactions: List<TransactionItem>): Boolean {
        val names = interactor.getAccountNames()
        var accountNamesChanged = false
        for (transaction in transactions) {
            val name = names[transaction.transaction.sourceAccount]
            if (!accountNamesChanged) accountNamesChanged = transaction.name != name
            transaction.name = name
        }

        return accountNamesChanged
    }

    /**
     * Used for receive federation by account id
     */
    private fun getStellarAccounts(transactions: MutableList<TransactionItem>) {

        // First create list of unique accounts (don't existing in cached list).
        val accountList = mutableListOf<Account>()
        transactions.forEach {
            if (cachedStellarAccounts.find { account -> account.address == it.transaction.sourceAccount } == null
                && accountList.find { account -> account.address == it.transaction.sourceAccount } == null) {
                accountList.add(Account(it.transaction.sourceAccount, it.federation))
            }
        }
        stellarAccountsDisposable?.dispose()
        stellarAccountsDisposable = Observable.fromIterable(accountList)
            .subscribeOn(Schedulers.io())
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
                transactions.forEachIndexed { index, transactionItem ->
                    val federation =
                        cachedStellarAccounts.find { account -> account.address == transactionItem.transaction.sourceAccount }
                            ?.federation
                    transactions[index].federation = federation
                }
                viewState.showTransactionList(transactions, !nextPageUrl.isNullOrEmpty())
            }, {
                // Ignore.
            })

        unsubscribeOnDestroy(stellarAccountsDisposable!!)
    }

    fun handleTransactionResult() {
        refreshCalled()
    }

    fun refreshCalled() {
        newLoadTransactions = true
        nextPageUrl = null

        loadTransactions()
    }

    fun transactionItemClicked(transactionItem: TransactionItem) {
        viewState.showTransactionDetails(transactionItem)
    }

    fun transactionItemLongClicked(transactionItem: TransactionItem) {
        transactionHashToDelete = transactionItem.hash
        viewState.showDeclineTransactionDialog()
    }

    fun addTransactionClicked() {
        viewState.showImportXdrScreen()
    }

    fun clearClicked() {
        val dialogTag: String
        val message: String
        val positiveBtnText = AppUtil.getString(R.string.remove_transactions_invalid_action)
        val neutralBtnText: String?
        val negativeBtnText: String

        when (status) {
            Status.SIGNED -> {
                dialogTag = AlertDialogFragment.DialogFragmentIdentifier.CLEAR_SIGNED_TRANSACTIONS
                message = AppUtil.getString(R.string.remove_transactions_signed_description)
                neutralBtnText = null
                negativeBtnText = AppUtil.getString(R.string.cancel_action)
            }

            else -> {
                dialogTag = AlertDialogFragment.DialogFragmentIdentifier.CLEAR_PENDING_TRANSACTIONS
                message = AppUtil.getString(R.string.remove_transactions_pending_description)
                neutralBtnText = AppUtil.getString(R.string.cancel_action)
                negativeBtnText = AppUtil.getString(R.string.remove_transactions_all_action)
            }
        }

        viewState.showClearTransactionsDialog(
            dialogTag,
            message,
            positiveBtnText,
            neutralBtnText,
            negativeBtnText
        )
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.CLEAR_PENDING_TRANSACTIONS -> {
                // Pending: Decline Invalid
                clearTransactions(
                    status,
                    notEnoughSignersWeight = null,
                    submittedAtIsNull = true,
                    sequenceOutdatedAtIsNull = false
                )
            }
            AlertDialogFragment.DialogFragmentIdentifier.CLEAR_SIGNED_TRANSACTIONS -> {
                // Signed: Decline Invalid
                clearTransactions(
                    status,
                    notEnoughSignersWeight = false,
                    submittedAtIsNull = true,
                    sequenceOutdatedAtIsNull = false
                )
            }
            AlertDialogFragment.DialogFragmentIdentifier.DECLINE_TRANSACTION -> declineTransaction(transactionHashToDelete)
        }
    }

    fun onAlertDialogNegativeButtonClicked(tag: String?) {
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.CLEAR_PENDING_TRANSACTIONS -> {
                // Pending: Remove All
                clearTransactions(
                    status,
                    notEnoughSignersWeight = null,
                    submittedAtIsNull = true,
                    sequenceOutdatedAtIsNull = null
                )
            }
        }
    }

    private fun clearTransactions(
        status: String,
        notEnoughSignersWeight: Boolean?,
        submittedAtIsNull: Boolean?,
        sequenceOutdatedAtIsNull: Boolean?
    ) {
        unsubscribeOnDestroy(
            interactor.cancelTransactions(
                status,
                notEnoughSignersWeight,
                submittedAtIsNull,
                sequenceOutdatedAtIsNull
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog(true)
                }
                .doOnEvent {
                    viewState.showProgressDialog(false)
                }
                .subscribe({
                    refreshCalled()
                }, {
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showErrorMessage(it.details)
                        }
                        is UserNotAuthorizedException -> {
                            when (it.action) {
                                UserNotAuthorizedException.Action.AUTH_REQUIRED -> eventProviderModule.authEventSubject.onNext(
                                    Auth()
                                )
                                else -> clearTransactions(
                                    status,
                                    notEnoughSignersWeight,
                                    submittedAtIsNull,
                                    sequenceOutdatedAtIsNull
                                )
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

    private fun declineTransaction(hash: String?) {
        if (hash.isNullOrEmpty() || cancellationInProcess) {
            return
        }
        unsubscribeOnDestroy(
            interactor.cancelTransaction(hash)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    cancellationInProcess = true
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showProgressDialog(false)
                    cancellationInProcess = false
                }
                .subscribe({
                    refreshCalled()
                }, {
                    when (it) {
                        is UserNotAuthorizedException -> {
                            when (it.action) {
                                UserNotAuthorizedException.Action.AUTH_REQUIRED -> eventProviderModule.authEventSubject.onNext(
                                    Auth()
                                )
                                else -> declineTransaction(hash)
                            }
                        }
                        is InternalException -> viewState.showErrorMessage(
                            AppUtil.getString(
                                R.string.api_error_internal_submit_transaction
                            )
                        )
                        is HttpNotFoundException -> {
                            viewState.showErrorMessage(AppUtil.getString(R.string.transaction_details_msg_already_signed_or_declined))
                        }
                        is DefaultException -> viewState.showErrorMessage(it.details)
                        else -> {
                            viewState.showErrorMessage(it.message ?: "")
                        }
                    }
                })
        )
    }

    fun onListScrolled(
        totalItemCount: Int,
        firstVisibleItemPosition: Int,
        lastVisibleItemPosition: Int
    ) {
        if (!isLoading && !nextPageUrl.isNullOrEmpty()) {
            if (lastVisibleItemPosition >= totalItemCount - LIMIT_PAGE_SIZE / 2 && firstVisibleItemPosition >= 0) {
                loadTransactions()
            }
        }
    }
}