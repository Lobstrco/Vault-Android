package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details

import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.CreateAccountOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.tsmapper.presentation.util.Constant.TransactionType.AUTH_CHALLENGE
import com.lobstr.stellar.tsmapper.presentation.util.TsUtil
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.HttpNotFoundException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.domain.operation_details.OperationDetailsInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT_SHORT
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
                when (transactionItem.transaction.transactionType) {
                    AUTH_CHALLENGE -> R.string.text_transaction_challenge
                    else -> R.string.title_toolbar_transaction_details
                }
            )
            return
        }

        registerEventProvider()

        val operation: Operation = transactionItem.transaction.operations[position]
        operationFields = operation.getFields(AppUtil.getAppContext())

        viewState.setupToolbarTitle(
            when {
                transactionItem.transaction.operations.size == 1 && transactionItem.transaction.transactionType == AUTH_CHALLENGE -> {
                    R.string.text_transaction_challenge // Specific case for the 'Single Operation' Challenge Transaction.
                }
                else -> TsUtil.getTransactionOperationName(operation)
            }
        )

        // Apply Operation Source Account in cases when Transaction Source Account doesn't equal it.
        if (transactionItem.transaction.sourceAccount != operation.sourceAccount) {
            operation.applyOperationSourceAccountTo(AppUtil.getAppContext(),operationFields)
        }

        viewState.initRecycledView(operationFields)

        checkAccountNames()
        getStellarAccounts()
    }

    /**
     * Check Cashed Account Names for the Operation Fields.
     */
    private fun checkAccountNames () {
        unsubscribeOnDestroy(
            Single.fromCallable {
                var needUpdateFields = false
                val names = interactor.getAccountNames()

                operationFields.forEach {
                    if (AppUtil.isValidAccount(it.tag as? String)) {
                        val cachedName = names[it.value]
                        it.value = if(cachedName.isNullOrEmpty()) {
                            it.value
                        } else {
                            needUpdateFields = true
                            cachedName.plus(" (${AppUtil.ellipsizeStrInMiddle(it.value, PK_TRUNCATE_COUNT_SHORT)})")
                        }
                    }
                }
                return@fromCallable needUpdateFields
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if(it) viewState.notifyAdapter()
                    },
                    Throwable::printStackTrace
                )
        )
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

        unsubscribeOnDestroy(
            eventProviderModule.updateEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Update.Type.ACCOUNT_NAME -> {
                            checkAccountNames()
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
                AppUtil.isValidAccount(it.tag as? String)
            }
            .flatMapSingle { field ->
                interactor.getStellarAccount(field.tag as String).onErrorResumeNext { throwable ->
                    when (throwable) {
                        // Handle only Not Found exception.
                        is HttpNotFoundException -> {
                            Single.fromCallable { Account(field.tag as String) }
                        }
                        else -> Single.error(throwable)
                    }
                }
            }
            .filter { it.federation != null }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ account ->
                destinations.filter { field -> field.tag == account.address }.forEach { field ->
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

    /**
     * @param key Reserved for future implementations.
     * @param value Reserved for future implementations.
     * @param tag Additional info for field (e.g. Asset for asset code)
     */
    fun operationItemClicked(key: String, value: String?, tag: Any?) {
        when {
            AppUtil.isValidAccount(tag as? String) -> tag?.let {viewState.showEditAccountDialog(tag as String) }
            tag is Asset -> viewState.showAssetInfoDialog(tag.assetCode, tag.assetIssuer)
        }
    }
}