package com.lobstr.stellar.vault.presentation.home.transactions.details

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.*
import com.lobstr.stellar.vault.domain.transaction_details.TransactionDetailsInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.transaction_details.TransactionDetailsModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.CONFIRM_TRANSACTION
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.DENY_TRANSACTION
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.CANCELLED
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.IMPORT_XDR
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.PENDING
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.SIGNED
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class TransactionDetailsPresenter(private var transactionItem: TransactionItem) :
    BasePresenter<TransactionDetailsView>() {

    @Inject
    lateinit var interactor: TransactionDetailsInteractor

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    private var confirmationInProcess = false
    private var cancellationInProcess = false

    private var stellarAccountsSubscription: Disposable? = null
    private val cashedStellarAccounts: MutableList<Account> = mutableListOf()

    init {
        LVApplication.appComponent.plusTransactionDetailsComponent(TransactionDetailsModule())
            .inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        registerEventProvider()
        viewState.setupToolbarTitle(R.string.title_toolbar_transaction_details)
        viewState.initSignersRecycledView()
        prepareUiAndOperationsList()
        getTransactionSigners()
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.networkEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Network.Type.CONNECTED -> {
                            if (needCheckConnectionState) {
                                getTransactionSigners()
                            }
                            cancelNetworkWorker(false)
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun getTransactionSigners() {
        unsubscribeOnDestroy(
            interactor.getTransactionSigners(
                transactionItem.xdr!!,
                transactionItem.transaction.sourceAccount!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showSignersContainer(false)
                    viewState.showSignersProgress(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showSignersProgress(false)
                }
                .subscribe({
                    viewState.showSignersContainer(it.isNotEmpty())

                    // check cashed federation items
                    it.forEachIndexed { index, accountItem ->
                        val federation =
                            cashedStellarAccounts.find { account -> account.address == accountItem.address }
                                ?.federation
                        it[index].federation = federation
                    }
                    viewState.notifySignersAdapter(it)

                    // try receive federations for accounts
                    getStellarAccounts(it)
                }, {
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is UserNotAuthorizedException -> {
                            getTransactionSigners()
                        }
                        is DefaultException -> viewState.showMessage(it.details)
                        else -> {
                            viewState.showMessage(it.message ?: "")
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

                viewState.notifySignersAdapter(accounts)
            }, {
                // ignore
            })

        unsubscribeOnDestroy(stellarAccountsSubscription!!)
    }

    private fun prepareUiAndOperationsList() {
        // Handle transaction validation status.
        viewState.setTransactionValid(transactionItem.sequenceOutdatedAt.isNullOrEmpty())

        // Handle transaction status.
        when (transactionItem.status) {
            PENDING -> viewState.setActionBtnVisibility(true, true)
            CANCELLED -> viewState.setActionBtnVisibility(false, false)
            SIGNED -> viewState.setActionBtnVisibility(false, false)
            IMPORT_XDR -> viewState.setActionBtnVisibility(true, true)
        }

        // Prepare operations list or show single operation:
        // when transaction has only one operation - show operation details screen immediately, else - list.
        if (transactionItem.transaction.operations.size > 1) {
            viewState.showOperationList(transactionItem)
        } else {
            viewState.showOperationDetailsScreen(transactionItem, 0)
        }
    }

    fun btnConfirmClicked() {
        when {
            interactor.isTrConfirmationEnabled() -> viewState.showConfirmTransactionDialog()
            else -> confirmTransaction()
        }
    }

    fun btnDenyClicked() {
        when {
            interactor.isTrConfirmationEnabled() -> viewState.showDenyTransactionDialog()
            else -> denyTransaction()
        }
    }

    /**
     * Cases:
     * 1. Is Vault Transaction -> retrieve actual transaction -> sign it on horizon -> notify vault server
     * 2. Is Transaction from IMPORT_XDR: only sign it on horizon
     */
    private fun confirmTransaction() {
        if (confirmationInProcess) {
            return
        }

        var needAdditionalSignatures = false

        unsubscribeOnDestroy(
            interactor.retrieveActualTransaction(transactionItem)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    transactionItem = it
                    interactor.confirmTransactionOnHorizon(it.xdr!!)
                }
                .flatMap {
                    val envelopXdr = it.envelopeXdr.get()
                    val extras = it.extras
                    val transactionResultCode = extras?.resultCodes?.transactionResultCode

                    when {
                        envelopXdr == null -> throw HorizonException(
                            transactionResultCode!!
                        )
                        transactionResultCode != null && transactionResultCode != "tx_bad_auth" -> throw HorizonException(
                            transactionResultCode
                        )
                        transactionResultCode != null && transactionResultCode == "tx_bad_auth" -> needAdditionalSignatures =
                            true
                    }

                    interactor.confirmTransactionOnServer(
                        needAdditionalSignatures,
                        transactionItem.status,
                        it.hash,
                        envelopXdr
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    confirmationInProcess = true
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showProgressDialog(false)
                    confirmationInProcess = false
                }
                .subscribe({
                    // update transaction status
                    transactionItem = TransactionItem(
                        transactionItem.cancelledAt,
                        transactionItem.addedAt,
                        transactionItem.xdr,
                        transactionItem.signedAt,
                        transactionItem.hash,
                        transactionItem.getStatusDisplay,
                        SIGNED,
                        transactionItem.sequenceOutdatedAt,
                        transactionItem.transaction
                    )

                    viewState.successConfirmTransaction(
                        it,
                        needAdditionalSignatures,
                        transactionItem
                    )

                    // NOTE Update transaction screen after operation if needed: prepareUiAndOperationsList().

                    // Notify about transaction changed.
                    eventProviderModule.notificationEventSubject.onNext(
                        Notification(Notification.Type.TRANSACTION_COUNT_CHANGED, null)
                    )
                }, {
                    when (it) {
                        is HorizonException -> {
                            viewState.errorConfirmTransaction(it.details)
                        }
                        is UserNotAuthorizedException -> {
                            confirmTransaction()
                        }
                        is InternalException -> viewState.showMessage(
                            LVApplication.appComponent.context.getString(
                                R.string.api_error_internal_submit_transaction
                            )
                        )
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

    private fun denyTransaction() {
        // Check transaction status = IMPORT_XDR - transaction details showed for entered xdr.
        when (transactionItem.status) {
            IMPORT_XDR -> {
                viewState.successDenyTransaction(transactionItem)

                // Notify about transaction changed.
                eventProviderModule.notificationEventSubject.onNext(
                    Notification(Notification.Type.TRANSACTION_COUNT_CHANGED, null)
                )
            }
            else -> {
                if (cancellationInProcess) {
                    return
                }
                unsubscribeOnDestroy(
                    interactor.cancelTransaction(transactionItem.hash)
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
                            transactionItem = it

                            // NOTE Update transaction screen after operation if needed: prepareUiAndOperationsList().

                            viewState.successDenyTransaction(it)

                            // Notify about transaction changed.
                            eventProviderModule.notificationEventSubject.onNext(
                                Notification(Notification.Type.TRANSACTION_COUNT_CHANGED, null)
                            )
                        }, {
                            when (it) {
                                is UserNotAuthorizedException -> {
                                    denyTransaction()
                                }
                                is InternalException -> viewState.showMessage(
                                    LVApplication.appComponent.context.getString(
                                        R.string.api_error_internal_submit_transaction
                                    )
                                )
                                is DefaultException -> viewState.showMessage(it.details)
                                else -> {
                                    viewState.showMessage(it.message ?: "")
                                }
                            }
                        })
                )
            }
        }
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        when (tag) {
            DENY_TRANSACTION -> denyTransaction()
            CONFIRM_TRANSACTION -> confirmTransaction()
        }
    }

    fun signedAccountItemLongClicked(account: Account) {
        viewState.copyToClipBoard(account.address)
    }
}