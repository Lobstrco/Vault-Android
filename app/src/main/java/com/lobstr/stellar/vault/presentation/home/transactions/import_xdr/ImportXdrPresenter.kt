package com.lobstr.stellar.vault.presentation.home.transactions.import_xdr

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.import_xdr.ImportXdrInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.util.AppUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ImportXdrPresenter @Inject constructor(private val interactor: ImportXdrInteractor) : BasePresenter<ImportXdrView>() {

    private var transactionCreationInProcess = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.toolbar_import_xdr_title)
    }

    fun nextClicked(xdr: String?) {
        if (xdr.isNullOrEmpty()) {
            return
        }

        createTransactionFromXdr(xdr)
    }

    private fun createTransactionFromXdr(xdr: String?) {
        if (transactionCreationInProcess) {
            return
        }

        unsubscribeOnDestroy(
            interactor.createTransactionItem(xdr!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    transactionCreationInProcess = true
                    viewState.setSubmitEnabled(false)
                }
                .doOnEvent { _, _ ->
                    transactionCreationInProcess = false
                    viewState.setSubmitEnabled(true)
                }
                .subscribe({
                    viewState.showTransactionDetails(it)
                }, {
                    viewState.showFormError(
                        true,
                        AppUtil.getString(R.string.import_xdr_invalid_label)
                    )
                })
        )
    }

    fun xdrChanged(length: Int) {
        viewState.setSubmitEnabled(length > 0)
        viewState.showFormError(false, null)
    }
}