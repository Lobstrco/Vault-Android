package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.HttpNotFoundException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.domain.operation_details.OperationDetailsInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.CreateAccountOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionType.Item.AUTH_CHALLENGE
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class OperationDetailsPresenter @Inject constructor(
    private val interactor: OperationDetailsInteractor,
    private val eventProviderModule: EventProviderModule

) : BasePresenter<OperationDetailsView>() {

    lateinit var transactionItem: TransactionItem
    var position: Int = 0

    private lateinit var operationFields: MutableList<OperationField>

    private var stellarAccountsDisposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        // Check case, when operations list is empty.
        if (transactionItem.transaction.operations.isNullOrEmpty()) {
            viewState.setupToolbarTitle(
                when (transactionItem.transactionType) {
                    AUTH_CHALLENGE -> R.string.text_transaction_challenge
                    else -> R.string.title_toolbar_transaction_details
                }
            )
            return
        }

        registerEventProvider()

        val operation: Operation = transactionItem.transaction.operations[position]
        operationFields = operation.getFields()

        viewState.setupToolbarTitle(
            when {
                transactionItem.transaction.operations.size == 1 && transactionItem.transactionType == AUTH_CHALLENGE -> {
                    R.string.text_transaction_challenge // Specific case for the 'Single Operation' Challenge Transaction.
                }
                else -> AppUtil.getTransactionOperationName(operation)
            }
        )

        // Apply Operation Source Account in cases when Transaction Source Account doesn't equal it.
        if (transactionItem.transaction.sourceAccount != operation.sourceAccount) {
            operation.applyOperationSourceAccountTo(operationFields)
        }

        viewState.initRecycledView(operationFields)

        getStellarAccounts()
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.networkEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Network.Type.CONNECTED -> {
                            if (needCheckConnectionState) {
                                getStellarAccounts()
                            }
                            cancelNetworkWorker(false)
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    /**
     * Used for receive federation by account id.
     */
    private fun getStellarAccounts() {
        // Exclude some operations if needed.
        when(transactionItem.transaction.operations[position]) {
            is CreateAccountOperation -> return
        }

        // Get federations only for destination field key.
        val destinations = operationFields.filter { it.key == AppUtil.getString(R.string.op_field_destination) }
        if (destinations.isNullOrEmpty()) {
            return
        }
        stellarAccountsDisposable?.dispose()
        stellarAccountsDisposable = Observable.fromIterable(destinations)
            .subscribeOn(Schedulers.io())
            .filter {
                AppUtil.isPublicKey(it.value)
            }
            .flatMapSingle { field ->
                interactor.getStellarAccount(field.value!!).onErrorResumeNext { throwable ->
                    when (throwable) {
                        // Handle only Not Found exception.
                        is HttpNotFoundException -> {
                            Single.fromCallable { Account(field.value!!) }
                        }
                        else -> Single.error(throwable)
                    }
                }
            }
            .filter { it.federation != null }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ account ->
                destinations.filter { field -> field.value == account.address }.forEach { field ->
                    account.federation?.let {
                        val position = operationFields.indexOf(field)
                        if (position != -1) {
                            operationFields.add(position + 1, OperationField(AppUtil.getString(R.string.op_field_destination_federation), it))
                        }
                    }
                }

                viewState.notifyAdapter()
            }, {
                when (it) {
                    is NoInternetConnectionException -> {
                        handleNoInternetConnection()
                    }
                }
            })

        unsubscribeOnDestroy(stellarAccountsDisposable!!)
    }
}