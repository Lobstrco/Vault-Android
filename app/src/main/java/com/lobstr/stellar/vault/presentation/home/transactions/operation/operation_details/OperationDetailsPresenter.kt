package com.lobstr.stellar.vault.presentation.home.transactions.operation.operation_details

import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.CreateAccountOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.tsmapper.presentation.util.TsUtil
import com.lobstr.stellar.vault.data.error.exeption.HttpNotFoundException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.domain.operation_details.OperationDetailsInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
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

    var title: Int = Constant.Util.UNDEFINED_VALUE
    lateinit var operation: Operation
    lateinit var transactionSourceAccount: String

    private lateinit var operationFields: MutableList<OperationField>

    private var stellarAccountsDisposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(title.run {
           if (this == Constant.Util.UNDEFINED_VALUE) TsUtil.getTransactionOperationName(operation) else this
        })

        registerEventProvider()

        operationFields = operation.getFields(AppUtil.getAppContext())

        // Apply Operation Source Account in cases when Transaction Source Account doesn't equal it.
        if (transactionSourceAccount != operation.sourceAccount) {
            operation.applyOperationSourceAccountTo(AppUtil.getAppContext(),operationFields)
        }

        if (operationFields.isEmpty()) {
            viewState.showContent(false)
            return
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
                        val cachedName = names[it.tag]
                        val value = if(cachedName.isNullOrEmpty()) {
                            it.tag as? String
                        } else {
                            cachedName.plus(" (${AppUtil.ellipsizeStrInMiddle(it.tag as String, PK_TRUNCATE_COUNT_SHORT)})")
                        }
                        if (!needUpdateFields) needUpdateFields = it.value != value
                        it.value = value
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
        when (operation) {
            is CreateAccountOperation -> return
        }

        // Get federations only for destination field key.
        val destinations = operationFields.filter { it.key == AppUtil.getString(com.lobstr.stellar.tsmapper.R.string.op_field_destination) }
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
                            operationFields.add(position + 1, OperationField(AppUtil.getString(com.lobstr.stellar.tsmapper.R.string.op_field_destination_federation), it))
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
     * @param value Value from operation field.
     * @param tag Additional info for field (e.g. Asset for asset code)
     */
    fun operationItemClicked(key: String, value: String?, tag: Any?) {
        when {
            AppUtil.isValidAccount(tag as? String) -> tag?.also { viewState.showEditAccountDialog(it as String) }
            tag is Asset.CanonicalAsset -> viewState.showAssetInfoDialog(tag.assetCode, tag.assetIssuer)
            AppUtil.isValidContractID(value) -> value?.also { viewState.showContractInfoDialog(it) }
        }
    }
}