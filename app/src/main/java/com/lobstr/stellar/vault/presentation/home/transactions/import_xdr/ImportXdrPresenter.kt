package com.lobstr.stellar.vault.presentation.home.transactions.import_xdr

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.import_xdr.ImportXdrInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
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


    private var transactionCreationInProcess = false

    init {
        LVApplication.appComponent.plusImportXdrComponent(ImportXdrModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.title_toolbar_import_xdr)
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
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showProgressDialog(false)
                    transactionCreationInProcess = false
                }
                .subscribe({
                    viewState.showTransactionDetails(it)
                }, {
                    viewState.showFormError(
                        true,
                        LVApplication.appComponent.context.getString(R.string.msg_bad_xdr)
                    )
                })
        )
    }

    fun xdrChanged(length: Int) {
        viewState.setSubmitEnabled(length > 0)
        viewState.showFormError(false, null)
    }
}