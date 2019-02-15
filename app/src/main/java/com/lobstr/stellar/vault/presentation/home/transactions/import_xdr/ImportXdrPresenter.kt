package com.lobstr.stellar.vault.presentation.home.transactions.import_xdr

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.HorizonException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.import_xdr.ImportXdrInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.import_xdr.ImportXdrModule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class ImportXdrPresenter : BasePresenter<ImportXdrView>() {

    @Inject
    lateinit var interactor: ImportXdrInteractor

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    private var confirmationInProcess = false

    init {
        LVApplication.sAppComponent.plusImportXdrComponent(ImportXdrModule()).inject(this)
    }

    fun confirmClicked(xdr: String?) {
        if (xdr.isNullOrEmpty()) {
            return
        }

        confirmTransaction(xdr)
    }

    private fun confirmTransaction(xdr: String?) {
        if (confirmationInProcess) {
            return
        }

        var needAdditionalSignatures = false

        unsubscribeOnDestroy(
            interactor.confirmTransactionOnHorizon(xdr!!)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    val envelopXdr = it.envelopeXdr
                    val extras = it.extras
                    val transactionResultCode = extras?.resultCodes?.transactionResultCode

                    when {
                        envelopXdr == null -> throw HorizonException(transactionResultCode!!)
                        transactionResultCode != null && transactionResultCode != "tx_bad_auth" -> throw HorizonException(
                            transactionResultCode
                        )
                        transactionResultCode != null && transactionResultCode == "tx_bad_auth" -> needAdditionalSignatures =
                            true
                    }

                    interactor.confirmTransactionOnServer(
                        needAdditionalSignatures,
                        it.hash,
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
                    viewState.successConfirmTransaction(it, needAdditionalSignatures)

                    // Notify about transaction changed
                    eventProviderModule.notificationEventSubject.onNext(
                        Notification(Notification.Type.TRANSACTION_COUNT_CHANGED, null)
                    )
                }, {
                    when (it) {
                        is HorizonException -> {
                            // TODO handle "tx_bad_seq"
                            viewState.errorConfirmTransaction(it.details)
                        }
                        is UserNotAuthorizedException -> {
                            confirmTransaction(xdr)
                        }
                        is DefaultException -> {
                            // Checking: for handle Internal Stellar SDK exceptions (bad xdr and other): when for this instance
                            // of DefaultException base class is Default exception - it is SubClass of DefaultException
                            // like BadRequestException and etc, otherwise - some unknown error from SDK received
                            if (it.javaClass.superclass == DefaultException::class.java) {
                                viewState.showFormError(true, it.details)
                            } else {
                                viewState.showFormError(
                                    true,
                                    LVApplication.sAppComponent.context.getString(R.string.msg_bad_xdr)
                                )
                            }
                        }
                        else -> {
                            viewState.showFormError(
                                true,
                                it.message ?: ""
                            )
                        }
                    }
                })
        )
    }

    fun xdrChanged(length: Int) {
        viewState.setSubmitEnabled(length > 0)
        viewState.showFormError(false, null)
    }

}