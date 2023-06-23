package com.lobstr.stellar.vault.presentation.home.account_name

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.account_name.AccountNameInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.util.AppUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class AccountNamePresenter @Inject constructor(
    private val interactor: AccountNameInteractor,
    private val eventProviderModule: EventProviderModule
) : BasePresenter<AccountNameView>() {

    lateinit var publicKey: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        // Find account name for the relevant public key.
        unsubscribeOnDestroy(
            Single.fromCallable { return@fromCallable interactor.getAccountName(publicKey) ?: "" }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.setupTitle(
                        if (it.isNullOrEmpty()) AppUtil.getString(R.string.account_name_set_title) else AppUtil.getString(
                            R.string.account_name_change_title
                        )
                    )
                    viewState.setAccountName(it)
                }, Throwable::printStackTrace)
        )
    }

    fun saveClicked(name: String?) {
        Completable.fromCallable {
            interactor.saveAccountName(publicKey, name)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    // Notify about update content event.
                    eventProviderModule.updateEventSubject.onNext(Update(Update.Type.ACCOUNT_NAME))
                    viewState.closeScreen()
                },
                Throwable::printStackTrace
            )
    }

    fun cancelClicked() {
        viewState.closeScreen()
    }
}