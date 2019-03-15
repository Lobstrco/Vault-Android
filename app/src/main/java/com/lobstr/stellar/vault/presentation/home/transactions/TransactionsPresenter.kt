package com.lobstr.stellar.vault.presentation.home.transactions

import android.app.Activity
import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.transaction.TransactionInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.transaction.TransactionModule
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class TransactionsPresenter : BasePresenter<TransactionsView>() {

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    @Inject
    lateinit var transactionInteractor: TransactionInteractor

    private var nextPageUrl: String? = null
    private var newLoadTransactions = true
    private val transactions: MutableList<TransactionItem> = mutableListOf()

    init {
        LVApplication.sAppComponent.plusTransactionComponent(TransactionModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        registerEventProvider()
        viewState.setupToolbarTitle(R.string.title_toolbar_transactions)
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
                        Notification.Type.ADDED_NEW_TRANSACTION,
                        Notification.Type.ADDED_NEW_SIGNATURE,
                        Notification.Type.TRANSACTION_SUBMITTED -> {
                            //TODO improve logic in future
//                            val transactionItem = it.data as? TransactionItem
//                            if (transactionItem != null && !transactionItem.xdr.isNullOrEmpty()) {
//                                transactions.add(0, transactionItem)
//                                viewState.showTransactionList(transactions)
//                                viewState.hideEmptyState()
//                            }
                            refreshCalled()
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun loadTransactions() {
        unsubscribeOnDestroy(
            transactionInteractor.getPendingTransactionList(nextPageUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    if (newLoadTransactions) {
                        viewState.showProgress()
                        viewState.hideEmptyState()
                    }
                }
                .doOnEvent { _, _ -> viewState.hideProgress() }
                .subscribe({ result ->
                    if (newLoadTransactions) {
                        transactions.clear()
                    }

                    transactions.addAll(result.results)
                    if (transactions.isEmpty()) {
                        viewState.showEmptyState()
                    } else {
                        viewState.hideEmptyState()
                    }
                    nextPageUrl = result.next
                    newLoadTransactions = false
                    viewState.showTransactionList(transactions)
                }, {
                    if (transactions.isEmpty()) {
                        viewState.showEmptyState()
                    } else {
                        viewState.hideEmptyState()
                    }
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showErrorMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is UserNotAuthorizedException -> {
                            loadTransactions()
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

    internal fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }

        when (requestCode) {
            Constant.Code.TRANSACTION_DETAILS_FRAGMENT -> {
                //TODO handle transactionItem it if needed
                val transactionItem: TransactionItem? =
                    data?.getParcelableExtra(Constant.Extra.EXTRA_TRANSACTION_ITEM)

                when (data?.getIntExtra(Constant.Extra.EXTRA_TRANSACTION_STATUS, -1)) {
                    Constant.Transaction.SIGNED -> viewState.checkRateUsDialog()
                }

                refreshCalled()
            }
        }
    }

    fun refreshCalled() {
        newLoadTransactions = true
        nextPageUrl = null
        loadTransactions()
    }

    fun transactionItemClicked(transactionItem: TransactionItem) {
        viewState.showTransactionDetails(transactionItem)
    }

    fun addTransactionClicked() {
        viewState.showImportXdrScreen()
    }
}