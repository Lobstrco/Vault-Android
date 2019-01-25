package com.lobstr.stellar.vault.presentation.home.transactions.details

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.HorizonException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.transaction_details.TransactionDetailsInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.transaction_details.TransactionDetailsModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.DENY_TRANSACTION
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.CANCELLED
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.PENDING
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.SIGNED
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class TransactionDetailsPresenter(private var mTransactionItem: TransactionItem) :
    BasePresenter<TransactionDetailsView>() {

    @Inject
    lateinit var mInteractor: TransactionDetailsInteractor

    @Inject
    lateinit var mEventProviderModule: EventProviderModule

    private var confirmationInProcess = false
    private var cancellationInProcess = false
    private var operationList: MutableList<Int> = mutableListOf()

    init {
        LVApplication.sAppComponent.plusTransactionDetailsComponent(TransactionDetailsModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.title_toolbar_transaction_details)
        viewState.initRecycledView()
        prepareUiAndOperationsList()
    }

    private fun prepareUiAndOperationsList() {
        // handle transaction status
        when (mTransactionItem.status) {
            PENDING -> viewState.setActionBtnVisibility(true, true)
            CANCELLED -> viewState.setActionBtnVisibility(false, false)
            SIGNED -> viewState.setActionBtnVisibility(false, false)
        }

        // prepare operations list for show it
        operationList.clear()
        for (operation in mTransactionItem.transaction.operations) {
            val resId: Int = AppUtil.getTransactionOperationName(operation)
            if (resId != -1) {
                operationList.add(resId)
            }
        }
        viewState.setOperationsToList(operationList)
    }

    fun btnConfirmClicked() {
        confirmTransaction()
    }

    fun btnDenyClicked() {
        viewState.showDenyTransactionDialog()
    }

    private fun confirmTransaction() {
        if (confirmationInProcess) {
            return
        }

        var needAdditionalSignatures = false

        unsubscribeOnDestroy(
            mInteractor.confirmTransactionOnHorizon(mTransactionItem.xdr!!)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    val envelopXdr = it.envelopeXdr
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

                    mInteractor.confirmTransactionOnServer(
                        if (needAdditionalSignatures) null else true,
                        it.envelopeXdr
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    confirmationInProcess = true
                    viewState.showProgressDialog()
                }
                .doOnEvent { _, _ ->
                    viewState.dismissProgressDialog()
                    confirmationInProcess = false
                }
                .subscribe({
                    // update transaction status
                    mTransactionItem = TransactionItem(
                        mTransactionItem.cancelledAt,
                        mTransactionItem.addedAt,
                        mTransactionItem.xdr,
                        mTransactionItem.signedAt,
                        mTransactionItem.hash,
                        mTransactionItem.getStatusDisplay,
                        SIGNED,
                        mTransactionItem.transaction
                    )

                    viewState.successConfirmTransaction(it, needAdditionalSignatures, mTransactionItem)
                    prepareUiAndOperationsList()

                    // Notify about transaction changed
                    mEventProviderModule.notificationEventSubject.onNext(
                        Notification(Notification.Type.TRANSACTION_COUNT_CHANGED, null)
                    )
                }, {
                    when (it) {
                        is HorizonException -> {
                            // TODO handle "tx_bad_seq"
                            viewState.errorConfirmTransaction(it.details)
                        }
                        is UserNotAuthorizedException -> {
                            confirmTransaction()
                        }
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
        if (cancellationInProcess) {
            return
        }
        unsubscribeOnDestroy(
            mInteractor.cancelTransaction(mTransactionItem.hash)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    cancellationInProcess = true
                    viewState.showProgressDialog()
                }
                .doOnEvent { _, _ ->
                    viewState.dismissProgressDialog()
                    cancellationInProcess = false
                }
                .subscribe({
                    mTransactionItem = it
                    prepareUiAndOperationsList()
                    viewState.successDenyTransaction(it)

                    // Notify about transaction changed
                    mEventProviderModule.notificationEventSubject.onNext(
                        Notification(Notification.Type.TRANSACTION_COUNT_CHANGED, null)
                    )
                }, {
                    when (it) {
                        is UserNotAuthorizedException -> {
                            denyTransaction()
                        }
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

    fun operationItemClicked(position: Int) {
        viewState.showOperationDetailsScreen(mTransactionItem, position)
    }

    fun onAlertDialogPositiveButtonClick(tag: String?) {
        if (tag.isNullOrEmpty()) {
            return
        }

        when (tag) {
            DENY_TRANSACTION -> denyTransaction()
        }
    }
}