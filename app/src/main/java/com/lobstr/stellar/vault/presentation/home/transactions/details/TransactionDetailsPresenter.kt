package com.lobstr.stellar.vault.presentation.home.transactions.details

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.transaction_details.TransactionDetailsInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dager.module.transaction_details.TransactionDetailsModule
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.CANCELLED
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.PENDING
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.SIGNED
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class TransactionDetailsPresenter(private var transactionItem: TransactionItem) :
    BasePresenter<TransactionDetailsView>() {

    @Inject
    lateinit var mInteractor: TransactionDetailsInteractor

    private var confirmationInProcess = false

    private var cancellationInProcess = false

    init {
        LVApplication.sAppComponent.plusTransactionDetailsComponent(TransactionDetailsModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupToolbarTitle(R.string.transaction_details)
        prepareUI()
    }

    private fun prepareUI() {
        when (transactionItem.status) {
            PENDING -> viewState.setActionBtnVisibility(true, true)
            CANCELLED -> viewState.setActionBtnVisibility(false, false)
            SIGNED -> viewState.setActionBtnVisibility(false, false)
        }
    }

    fun btnConfirmClicked() {
        confirmTransaction()
    }

    fun btnDenyClicked() {
        denyTransaction()
    }

    private fun confirmTransaction() {
        if (confirmationInProcess) {
            return
        }

        var needAdditionalSignatures = false

        unsubscribeOnDestroy(
            mInteractor.confirmTransactionOnHorizon(transactionItem.xdr!!)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    val envelopXdr = it.envelopeXdr
                    val extras = it.extras
                    val transactionResultCode = extras?.resultCodes?.transactionResultCode

                    when {
                        envelopXdr == null -> throw Throwable(transactionResultCode)
                        transactionResultCode != null && transactionResultCode != "tx_bad_auth" -> throw Throwable(
                            transactionResultCode
                        )
                        transactionResultCode != null && transactionResultCode == "tx_bad_auth" -> needAdditionalSignatures =
                                true
                    }

                    mInteractor.confirmTransactionOnServer(
                        needAdditionalSignatures,
                        it.envelopeXdr
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog()
                }
                .doOnEvent { _, _ ->
                    viewState.dismissProgressDialog()
                    confirmationInProcess = false
                }
                .subscribe({
                    if (needAdditionalSignatures) {
                        viewState.notifyAboutNeedAdditionalSignatures(it)
                    } else {
                        viewState.succesConfirmTransaction(it)
                    }
                    prepareUI()
                }, {
                    when (it) {
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
            mInteractor.cancelTransaction(transactionItem.hash)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog()
                }
                .doOnEvent { _, _ ->
                    viewState.dismissProgressDialog()
                    cancellationInProcess = false
                }
                .subscribe({
                    transactionItem = it
                    prepareUI()
                    viewState.succesDenyTransaction(it)
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
}