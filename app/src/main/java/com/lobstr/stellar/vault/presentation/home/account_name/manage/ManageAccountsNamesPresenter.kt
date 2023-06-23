package com.lobstr.stellar.vault.presentation.home.account_name.manage

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.account_name.manage.ManageAccountsNamesInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ManageAccountsNamesPresenter @Inject constructor(
    private val interactor: ManageAccountsNamesInteractor,
    private val eventProviderModule: EventProviderModule
) : BasePresenter<ManageAccountsNamesView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.manage_nicknames_title)
        registerEventProvider()
        viewState.initRecycledView()
        getAccountsNames()
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.updateEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Update.Type.ACCOUNT_NAME -> {
                            getAccountsNames()
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun getAccountsNames() {
        unsubscribeOnDestroy(Single.fromCallable {
            interactor.getAccountNames()
        }
            .map { namesMap ->
                namesMap.map { map ->
                    Account(address = map.key, name = map.value)
                }.sortedBy { it.name }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState.showProgress(true) }
            .doOnEvent { _, _ -> viewState.showProgress(false) }
            .subscribe({
                viewState.showEmptyState(it.isEmpty())
                viewState.showAccountsNamesList(it)
            }, { throwable ->
                throwable.printStackTrace()
            })
        )
    }

    fun addAccountNameClicked() {
        viewState.showAddAccountNameScreen()
    }

    fun accountItemClicked(account: Account) {
        viewState.showEditAccountDialog(account.address)
    }

    fun accountItemLongClicked(account: Account) {
        viewState.copyToClipBoard(account.address)
    }

    fun onRefreshCalled() {
        getAccountsNames()
    }
}