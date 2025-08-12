package com.lobstr.stellar.vault.presentation.home.dashboard.account

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.domain.accounts.AccountsInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.account.AccountDialogItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class AccountsPresenter @Inject constructor(
    private val interactor: AccountsInteractor
) : BasePresenter<AccountsView>() {

    init {
        val currentPublicKey = interactor.getCurrentPublicKey()
        val keys = interactor.getPublicKeyList()

        viewState.showAddAccountButton(keys.size != Constant.Util.PUBLIC_KEY_LIMIT)

        val accountItems = keys.map {
            AccountDialogItem(
                address = it.first,
                name = getAccountName(it.second + 1, it.first),
                isChecked = it.first == currentPublicKey
            )
        }

        viewState.initList(accountItems, accountItems.indexOfFirst { it.isChecked })
    }

    private fun getAccountName(index: Int, publicKey: String): String =
        interactor.getAccountNames().let {
            when {
                it[publicKey].isNullOrEmpty() -> AppUtil.getString(
                    R.string.accounts_item_name_title,
                    index
                )
                else -> it[publicKey]!!
            }
        }

    fun onItemClicked(account: AccountDialogItem) {
        if (interactor.getCurrentPublicKey() != account.address) {
            interactor.saveNewKeyData(account.address)
            viewState.notifyHomeActivity()
        }

        viewState.dismissDialog()
    }

    fun addNewAccountClicked() {
        interactor.generateNewKeyPair()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState.showProgressDialog(true) }
            .doOnEvent { _, _ -> viewState.showProgressDialog(false) }
            .subscribe({
                viewState.notifyHomeActivity()
                viewState.dismissDialog()
            }, {
                when (it) {
                    is DefaultException -> viewState.showErrorMessage(it.details)
                    else -> viewState.showErrorMessage(it.message)
                }
            })
    }
}