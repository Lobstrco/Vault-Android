package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.edit_account.EditAccountInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class EditAccountPresenter @Inject constructor(
    private val interactor: EditAccountInteractor,
    private val eventProviderModule: EventProviderModule
) : BasePresenter<EditAccountView>() {

    lateinit var publicKey: String

    var manageAccountName: Boolean = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        if (manageAccountName) {
            // Find account name for the relevant public key.
            unsubscribeOnDestroy(
                Single.fromCallable { interactor.getAccountName(publicKey) ?: "" }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        viewState.setAccountActionButton(
                            if (it.isNullOrEmpty()) AppUtil.getString(R.string.text_tv_set_account_name) else AppUtil.getString(
                                R.string.text_tv_change_account_name
                            )
                        )
                        viewState.showClearAccountButton(!it.isNullOrEmpty())
                        viewState.showNetworkExplorerButton(publicKey != interactor.getCurrentPublicKey())
                    }, Throwable::printStackTrace)
            )
        }
    }

    fun setNickNameClicked() {
        viewState.showSetNickNameFlow(publicKey)
        viewState.closeScreen()
    }

    fun clearNickNameClicked() {
        Completable.fromCallable {
            interactor.clearAccountName(publicKey)
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

    fun copyPublicKeyClicked() {
        viewState.copyToClipBoard(publicKey)
        viewState.closeScreen()
    }

    fun openExplorerClicked() {
        viewState.openExplorer(Constant.Explorer.ACCOUNT.plus(publicKey))
        viewState.closeScreen()
    }
}